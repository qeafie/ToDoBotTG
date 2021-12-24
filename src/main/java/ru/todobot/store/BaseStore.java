package ru.todobot.store;

import java.time.LocalDate;
import java.util.List;

public interface BaseStore {

    void save(LocalDate date, String deal);
    List<String> selectAll(LocalDate date);
    void deleteAll(LocalDate date);
    void delete(LocalDate date, int index);
}
