package com.berghella.daniele.edu_hub.dao;

import com.berghella.daniele.edu_hub.model.Subject;
import com.berghella.daniele.edu_hub.utility.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SubjectDAO {
    private final Connection connection = DatabaseConnection.getInstance().getConnection();

    public void createSubject(Subject subject){
        String insertSubjectSQL = "INSERT INTO subject(id, name, description) " + "VALUES (?, ?, ?);";
        try {
            PreparedStatement psInsertSubject = connection.prepareStatement(insertSubjectSQL);
            psInsertSubject.setObject(1, subject.getId());
            psInsertSubject.setString(2, subject.getName());
            psInsertSubject.setString(3, subject.getDescription());
            psInsertSubject.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String getAllSubjectsSQL = "SELECT * FROM subject";
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(getAllSubjectsSQL);
            while (rs.next()){
                Subject subject = new Subject(
                        rs.getString("name"),
                        rs.getString("description"));
                subject.setId(UUID.fromString(rs.getString("id")));
                subjects.add(subject);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subjects;
    }

    public Optional<Subject> getSubjectById(UUID id) {
        String selectSubjectByIdSQL = "SELECT * FROM subject WHERE id = ?";
        try {
            PreparedStatement psSelectSubjectById = connection.prepareStatement(selectSubjectByIdSQL);
            psSelectSubjectById.setObject(1, id);
            ResultSet rs = psSelectSubjectById.executeQuery();
            if (rs.next()) {
                Subject subject = new Subject();
                subject.setId(id);
                subject.setName(rs.getString("name"));
                subject.setDescription(rs.getString("description"));
                return Optional.of(subject);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Subject updateSubjectById(Subject updatedSubject, UUID oldSubjectId) {
        if (getSubjectById(oldSubjectId).isPresent()){
            StringBuilder sql = new StringBuilder("UPDATE subject SET ");
            List<Object> parameters = new ArrayList<>();

            if (updatedSubject.getName() != null) {
                sql.append("name = ?, ");
                parameters.add(updatedSubject.getName());
            }
            if (updatedSubject.getDescription() != null) {
                sql.append("description = ?, ");
                parameters.add(updatedSubject.getDescription());
            }

            sql.setLength(sql.length() - 2);
            sql.append(" WHERE id = ?");
            parameters.add(oldSubjectId);
            try {
                PreparedStatement psUpdateSubject = connection.prepareStatement(sql.toString());
                for (int i = 0; i < parameters.size(); i++) {
                    psUpdateSubject.setObject(i + 1, parameters.get(i));
                }
                psUpdateSubject.executeUpdate();
                return getSubjectById(oldSubjectId).orElse(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean isDeletedSubjectById(UUID id) {
        String deleteSubjectSQL = "DELETE FROM subject WHERE id = ?";
        try {
            PreparedStatement psDeleteSubject = connection.prepareStatement(deleteSubjectSQL);
            psDeleteSubject.setObject(1, id);
            int rowsAffected = psDeleteSubject.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting subject with ID: " + id);
        }
    }
}
