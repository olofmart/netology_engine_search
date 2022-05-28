package ru.olmart;

import ru.olmart.PageEntry;

import java.util.List;

public interface SearchEngine {
    List<PageEntry> search(String word);
}
