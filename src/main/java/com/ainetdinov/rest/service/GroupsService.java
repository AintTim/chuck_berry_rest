package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;

@Getter
public class GroupsService extends EntityService<Group> {
    private final StudentService studentService;

    public GroupsService(List<Group> groups, ValidatorService<Group> validator, StudentService studentService) {
        super(groups, validator);
        this.studentService = studentService;
    }

    @Override
    public List<Group> getEntities() {
        //TODO: Добавить обработку обновленных студентов
        synchronized (this) {
            entities.forEach(group -> {
                if (!validateStudentsPresence(group.getStudents())) {
                    group.getStudents().removeIf(student -> !studentService.getEntities().contains(student));
                }
            });
        }
        return super.getEntities();
    }

    public boolean addGroup(Group group) {
        synchronized (entities) {
            if (validateEntity(group, validator::validate, this::isUnique) && validateStudentsPresence(group.getStudents())) {
                entities.add(group);
                return true;
            } else {
                return false;
            }
        }
    }

    public void addStudentsToGroup(List<Student> students, int groupId) {
        Group group = getEntity(g -> g.getId() == groupId);
        synchronized (entities) {
            if (validateStudentsPresence(students)) {
                students.stream()
                        .filter(s -> isUniqueStudent(s, group))
                        .forEach(group.getStudents()::add);
            }
        }
    }

    private boolean isUniqueStudent(Student student, Group group) {
        return !group.getStudents().contains(student);
    }

    private boolean validateStudentsPresence(List<Student> students) {
        return new HashSet<>(studentService.getEntities()).containsAll(students);
    }
}
