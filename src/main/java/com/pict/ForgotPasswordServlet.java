package com.pict;

import com.pict.database.DatabaseConnection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import javax.mail.internet.InternetAddress;
import java.sql.*;
import java.util.*;
import javax.mail.internet.MimeMessage;
import  javax.mail.Session;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.PasswordAuthentication;
import javax.servlet.http.HttpSession;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import static java.lang.System.out;


@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {

    private Connection connection;

    public ForgotPasswordServlet(){
        super();
        connection= DatabaseConnection.getDatabaseConnection();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection con;
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mentor_sys", "hello", "hello");
            out.println("SL3 " + "database successfully opened.");
            PreparedStatement preparedStatement = null;
            ArrayList<String> error = new ArrayList<>();
            String email="";
            int roll_no=0;
            int flag=0;
            email=request.getParameter("email");
            String roll_no1=request.getParameter("roll_no");
            out.println(email);
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";
            Pattern pat = Pattern.compile(emailRegex);
            if(email=="" || roll_no1=="") {
                if(email=="")
                    error.add("please Enter an email id");
                if(roll_no1=="")
                    error.add("please Enter the roll number");
                HttpSession session = request.getSession();
                session.setAttribute("getAlert", error);//Just initialize a random variable.
                response.sendRedirect("/jsp/forgot_password.jsp");
            }
            else {
                roll_no= Integer.parseInt(roll_no1);
                if(pat.matcher(email).matches()){
                    preparedStatement=con.prepareStatement("select stud_password from student where stud_email=? and stud_roll_no=?");
                    preparedStatement.setString(1,email);
                    preparedStatement.setString(2,roll_no1);
                    ResultSet rs=preparedStatement.executeQuery();
                    if(rs.next()){
                        out.println(email);
                        out.println(roll_no1);
                        HttpSession session = request.getSession();
                        session.setAttribute("EmailID",email);
                        session.setAttribute("rollNumber",roll_no1);
                        String password=rs.getString("stud_password");
                        out.println("password: "+password);
                        session.setAttribute("getAlert", password);//Just initialize a random variable.
//                        request.getRequestDispatcher("/jsp/reset_password.jsp").forward(request,response);
                        SendMailServlet.sendMail(request,response, email);
                        flag=1;
                    }
                    else {
                        error.add("Invalid Roll Number or E-mail ID");
                        HttpSession session = request.getSession();
                        session.setAttribute("getAlert", error);//Just initialize a random variable.
                        response.sendRedirect("/jsp/forgot_password.jsp");
                    }
                }
                else{
                    error.add("Invalid Email ID");
                    HttpSession session = request.getSession();
                    session.setAttribute("getAlert", error);//Just initialize a random variable.
                    response.sendRedirect("/jsp/forgot_password.jsp");
                }
            }
            if(flag==1)
                response.sendRedirect("https://accounts.google.com/signin/v2/identifier?flowName=GlifWebSignIn&flowEntry=ServiceLogin");
        }
        catch (SQLException e){
            out.println("SQLException caught: " +e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
