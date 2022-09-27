package ca.neilwhite.hrservice.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("employees")
public class Employee {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String position;

    @Column("is_full_time")
    private boolean fullTime;

    public static Employee fromRow(Map<String, Object> row) {
        if (row.get("e_id") != null) {
            return Employee.builder()
                    .id((Long.parseLong(row.get("e_id").toString())))
                    .firstName((String) row.get("e_firstName"))
                    .lastName((String) row.get("e_lastName"))
                    .position((String) row.get("e_position"))
                    .fullTime((Boolean) row.get("e_isFullTime"))
                    .build();
        } else {
            return null;
        }

    }

    public static Employee managerFromRow(Map<String, Object> row) {
        if (row.get("m_id") != null) {
            return Employee.builder()
                    .id((Long.parseLong(row.get("m_id").toString())))
                    .firstName((String) row.get("m_firstName"))
                    .lastName((String) row.get("m_lastName"))
                    .position((String) row.get("m_position"))
                    .fullTime((Boolean) row.get("m_isFullTime"))
                    .build();
        } else {
            return null;
        }
    }
}
