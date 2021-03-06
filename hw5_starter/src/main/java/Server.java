import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Employer;
import model.Job;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {

    private static Dao getEmployerORMLiteDao() throws SQLException {
        final String URI = "jdbc:sqlite:./JBApp.db";
        ConnectionSource connectionSource = new JdbcConnectionSource(URI);
        TableUtils.createTableIfNotExists(connectionSource, Employer.class);
        return DaoManager.createDao(connectionSource, Employer.class);
    }


    private static Dao getJobORMLiteDao() throws SQLException {
        final String URI = "jdbc:sqlite:./JBApp.db";
        ConnectionSource connectionSource = new JdbcConnectionSource(URI);
        TableUtils.createTableIfNotExists(connectionSource, Job.class);
        return DaoManager.createDao(connectionSource, Job.class);
    }

    public static void main(String[] args) {

        final int PORT_NUM = 7000;
        Spark.port(PORT_NUM);



        Spark.get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            if (req.cookie("username") != null)
                model.put("username", req.cookie("username"));
                model.put("color", req.cookie("color"));
            return new ModelAndView(model, "public/index.vm");
        }, new VelocityTemplateEngine());

        Spark.post("/", (req, res) -> {
            String username = req.queryParams("username");
            String color = req.queryParams("color");
            res.cookie("username", username);
            res.cookie("color", color);
            res.redirect("/");
            return null;
        });

        Spark.get("/employers", (req, res) -> {
            List<Employer> ls = getEmployerORMLiteDao().queryForAll();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("employers", ls);
            return new ModelAndView(model, "public/employers.vm");
        }, new VelocityTemplateEngine());

        Spark.get("/jobs", (req, res) -> {
            List<Job> ls = getJobORMLiteDao().queryForAll();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("jobs", ls);
            return new ModelAndView(model, "public/jobs.vm");
        }, new VelocityTemplateEngine());

        Spark.post("/employers", (req, res) -> {
            String name = req.queryParams("name");
            String sector = req.queryParams("sector");
            String summary = req.queryParams("summary");
            Employer em = new Employer(name, sector, summary);
            getEmployerORMLiteDao().create(em);
            res.status(201);
            res.type("application/json");
            return new Gson().toJson(em.toString());
        });

        Spark.get("/addemployers", (req, res) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            return new ModelAndView(model, "public/addemployers.vm");
        }, new VelocityTemplateEngine());

        Spark.get("/addjobs", (req, res) -> {
            List<Employer> ls = getEmployerORMLiteDao().queryForAll();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("employers", ls);
            return new ModelAndView(model, "public/addjobs.vm");
        }, new VelocityTemplateEngine());

        Spark.post("/jobs", (req, res) -> {
            String title = req.queryParams("title");
            Date datePosted = new SimpleDateFormat("yyyy-MM-dd").parse(req.queryParams("date-posted"));
            Date deadline = new SimpleDateFormat("yyyy-MM-dd").parse(req.queryParams("deadline"));
            String domain = req.queryParams("domain");
            String location = req.queryParams("location");
            boolean fullTime = Boolean.parseBoolean(req.queryParams("full-time"));
            boolean salaryBased = Boolean.parseBoolean(req.queryParams("salary-based"));
            String requirements = req.queryParams("requirements");
            int payment = Integer.parseInt(req.queryParams("payment"));
            Employer employer = (Employer) getEmployerORMLiteDao().queryForEq("name", req.queryParams("employer")).get(0);
            System.out.println(employer.toString());
            Job jb = new Job(title, datePosted, deadline, domain, location, fullTime, salaryBased, requirements, payment, employer);

            getJobORMLiteDao().create(jb);

            res.status(201);
            res.type("application/json");
            return new Gson().toJson(jb.toString());
        });

        Spark.get("/search", (req, res) -> {
            List<Job> ls = getJobORMLiteDao().queryForAll();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("search", ls);
            return new ModelAndView(model, "public/search.vm");
        }, new VelocityTemplateEngine());

        Spark.post("/search", (req, res) -> {
            String keyword = req.queryParams("keyword");
            List<Job> ls = getJobORMLiteDao().queryForAll();
            System.out.println(ls);
            List<Object> data = new ArrayList<Object>();
            for (int i = 0; i < ls.size(); i++) {
                Job current = ls.get(i);
                if ((current.getTitle().toLowerCase()).contains(keyword.toLowerCase()) || (current.getDomain().toLowerCase()).contains(keyword.toLowerCase()) || (current.getEmployer().getName().toLowerCase()).contains(keyword.toLowerCase())){
                    Job ret = new Job(current.getTitle(), current.getDatePosted(), current.getDeadline(), current.getDomain(), current.getLocation(), current.isFullTime(), current.isSalaryBased(), current.getRequirements(), current.getPayAmount(), current.getEmployer());
                    //System.out.println(ret.toString());
                    data.add(ret);
                }
            }
            return new Gson().toJson(data);
        });
    }
}
