package com.ainetdinov.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@Jacksonized
@EqualsAndHashCode
public class Schedule {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime end;
    private Long groupId;
    private Long teacherId;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("start", start.format(formatter))
                .append("end", end.format(formatter))
                .append("groupId", groupId)
                .append("teacherId", teacherId)
                .toString();
    }
}
