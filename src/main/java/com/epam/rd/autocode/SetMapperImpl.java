package com.epam.rd.autocode;

import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SetMapperImpl implements SetMapper<Set<Employee>> {
    @Override
    public Set<Employee> mapSet(ResultSet resultSet) {
        Set<Employee> employeeSet = new HashSet<>();
        try {
            while (resultSet.next()) {
                Employee employee = getEmployee(resultSet);
                employeeSet.add(employee);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeeSet;
    }

    private Employee getEmployee(ResultSet resultSet) throws SQLException {
        BigInteger id = BigInteger.valueOf(resultSet.getLong("ID"));
        String firstName = resultSet.getString("FIRSTNAME");
        String lastName = resultSet.getString("LASTNAME");
        String middleName = resultSet.getString("MIDDLENAME");
        Position position = Position.valueOf(resultSet.getString("POSITION"));

        LocalDate localDate = resultSet.getObject("HIREDATE", LocalDate.class);
        String salary = resultSet.getString("SALARY");

        Employee manager = getManager(resultSet, resultSet.getInt("MANAGER"));

        return new Employee(id, new FullName(firstName, lastName, middleName),
                position, localDate, new BigDecimal(salary), manager);
    }

    private Employee getManager(ResultSet resultSet, int id) throws SQLException {
        Employee manager = null;
        int currentRow = resultSet.getRow();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            if (resultSet.getInt(1) == id) {
                manager = getEmployee(resultSet);
                break;
            }
        }
        resultSet.absolute(currentRow);
        return manager;
    }

}
