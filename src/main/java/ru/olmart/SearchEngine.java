package ru.olmart;

import java.util.List;

public interface SearchEngine {
    List<PageEntry> search(String word);
}
