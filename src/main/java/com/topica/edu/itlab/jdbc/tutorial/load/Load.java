package com.topica.edu.itlab.jdbc.tutorial.load;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.topica.edu.itlab.jdbc.tutorial.annotation.Column;
import com.topica.edu.itlab.jdbc.tutorial.annotation.OneToMany;
import com.topica.edu.itlab.jdbc.tutorial.annotation.Table;
import com.topica.edu.itlab.jdbc.tutorial.entity.ClassEntity;
import com.topica.edu.itlab.jdbc.tutorial.entity.StudentEntity;

public class Load {
	
	public static <T> void loadList(ResultSet rs,List<T> listT,Class<T> type){
		try {
			while(rs.next()) {
				T t = type.newInstance();
				for (Field field : type.getDeclaredFields()) {
					field.setAccessible(true);
					Column column = field.getAnnotation(Column.class);
					Object value = null;
					if(column!=null) {
						value =rs.getObject(column.name(), field.getType());			
					}
					field.set(t, value);
				}
				listT.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * Eager load
	 */
	public static void loadListEager(ResultSet rs,List<ClassEntity> listClassEntity) {
		Map<Long, ClassEntity> hashMap = new HashMap<>();
		
		try {
			while(rs.next()) {
				String className = ClassEntity.class.getAnnotation(Table.class).name();
				String classStudentName = ClassEntity.class.getAnnotation(Table.class).name();
				String firstFieldName = ClassEntity.class.getDeclaredField("id").getAnnotation(Column.class).name();
				Long class_id =rs.getLong(className+"_"+firstFieldName);
				if(!hashMap.keySet().contains(class_id)) {
					
					ClassEntity classEntity = new ClassEntity();
					for (Field field : ClassEntity.class.getDeclaredFields()) {
						field.setAccessible(true);
						Column column = field.getAnnotation(Column.class);
						OneToMany oneToMany = field.getAnnotation(OneToMany.class);
						if(column!=null) {
							if(column.name().equals("id")) {
								field.set(classEntity, class_id);
							}
							else {						
								Object value =rs.getObject(className+"_"+column.name(), field.getType());	
								field.set(classEntity, value);
							}
						}
						if(oneToMany!=null) {
							classEntity.setListStudent(new ArrayList<StudentEntity>());
							StudentEntity studentEntity = new StudentEntity();
							for (Field fieldStudent : StudentEntity.class.getDeclaredFields()) {
								fieldStudent.setAccessible(true);
								Column columnStudent = fieldStudent.getAnnotation(Column.class);
								if(columnStudent!=null) {
									Object value =rs.getObject(classStudentName+"_"+columnStudent.name(), fieldStudent.getType());	
									fieldStudent.set(studentEntity, value);
								}
							}
							classEntity.getListStudent().add(studentEntity);
						}
					}		
					hashMap.put(class_id, classEntity);
				}
				else {
					StudentEntity studentEntity = new StudentEntity();
					for (Field fieldStudent : StudentEntity.class.getDeclaredFields()) {
						fieldStudent.setAccessible(true);
						Column columnStudent = fieldStudent.getAnnotation(Column.class);
						if(columnStudent!=null) {
							Object value =rs.getObject(classStudentName+"_"+columnStudent.name(), fieldStudent.getType());	
							fieldStudent.set(studentEntity, value);
						}
					}
					hashMap.get(class_id).getListStudent().add(studentEntity);
				}
			
//			Long class_id =rs.getLong("class_id");
//			if(!hashMap.keySet().contains(class_id)) {	
//				ClassEntity classEntity = new ClassEntity();
//				classEntity.setId(class_id);
//				classEntity.setName(rs.getString("class_name"));
//				classEntity.setListStudent(new ArrayList<StudentEntity>());
//				StudentEntity studentEntity = new StudentEntity();
//				studentEntity.setId(rs.getLong("student_id"));
//				studentEntity.setName(rs.getString("student_name"));
//				classEntity.getListStudent().add(studentEntity);
//				hashMap.put(class_id, classEntity);
//			}
//			else {
//				StudentEntity studentEntity = new StudentEntity();
//				studentEntity.setId(rs.getLong("student_id"));
//				studentEntity.setName(rs.getString("student_name"));
//				hashMap.get(class_id).getListStudent().add(studentEntity);
//			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			for (Entry<Long, ClassEntity> entry: hashMap.entrySet()) {
				listClassEntity.add(entry.getValue());
			}
		}
	}
}
