package com.topica.edu.itlab.jdbc.tutorial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.topica.edu.itlab.jdbc.tutorial.entity.ClassEntity;
import com.topica.edu.itlab.jdbc.tutorial.entity.StudentEntity;
import com.topica.edu.itlab.jdbc.tutorial.load.Load;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "school";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root";
        String dbpassword = "";
        String query1 = "SELECT * from Class";
        String query2 = "Select c.id as class_id,c.name as class_name,s.id as student_id, s.name as student_name from Class c, Student s where c.id = s.class_id";
        try {
        	//loading driver
			Class.forName(driver);
			// Connection set up with database named as user
			Connection conn = DriverManager.getConnection(url+dbName, userName, dbpassword);
			// Creating statement for the connection to use sql queries
			Statement st = conn.createStatement();
			
			/*
			 * 1. Lazy load
			 */
			System.out.println("Lazy load:");
			ResultSet rs = st.executeQuery(query1);			
			List<ClassEntity> listClassEntity = new ArrayList<ClassEntity>();
			//Lazy load data from database to listClassEntity
			Load.loadList(rs, listClassEntity,ClassEntity.class);
			System.out.println("when lazy load: "+listClassEntity);
			if(listClassEntity != null) {
				for (ClassEntity classEntity : listClassEntity) {
					//when read data of every ClassEntity, it listStudent = null, load data from student table in database
					if(classEntity.getListStudent() == null) {
						classEntity.setListStudent(new ArrayList<StudentEntity>());
						ResultSet rsStudent = st.executeQuery("SELECT * from Student WHERE class_id="+classEntity.getId());
						Load.loadList(rsStudent,classEntity.getListStudent(), StudentEntity.class);
					}
				}
				System.out.println("When read every data: "+listClassEntity);
			}
			else {
				System.out.println("List ClassEntity is null!");
			}
			/*
			 * 2. Eager load
			 */
			System.out.println("Eager load:");
			ResultSet rs2 =st.executeQuery(query2);
			List<ClassEntity> listClassEntity2 = new ArrayList<ClassEntity>();
			Load.loadListEager(rs2,listClassEntity2);
			System.out.println(listClassEntity2);
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
    }
}
