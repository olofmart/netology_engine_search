package ru.olmart;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {

    private final Map<String, List<PageEntry>> indexesMap;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        indexesMap = new HashMap<>();
        List<File> filesList = getFilesList(pdfsDir);
        for (File file : filesList) {
            var doc = new PdfDocument(new PdfReader(file));

            for (int page = 1; page <= doc.getNumberOfPages(); page++) {
                String text = PdfTextExtractor.getTextFromPage(doc.getPage(page));
                String[] words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> wordCount = wordFrequencyCount(words);

                for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                    if (indexesMap.containsKey(entry.getKey())) {
                        List<PageEntry> tmpList = indexesMap.get(entry.getKey());
                        tmpList.add(new PageEntry(file.getName(), page, entry.getValue()));
                        indexesMap.put(entry.getKey(), tmpList);
                        continue;
                    }

                    List<PageEntry> tmpList = new ArrayList<>();
                    tmpList.add(new PageEntry(file.getName(), page, entry.getValue()));
                    indexesMap.put(entry.getKey(), tmpList);
                }

            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> list = indexesMap.entrySet()
                .stream()
                .filter(set -> set.getKey().equals(word))
                .flatMap(m -> m.getValue().stream())
                .sorted((e1, e2) -> e1.compareTo(e2))
                .collect(Collectors.toList());
        return list;
    }

    private Map<String, Integer> wordFrequencyCount (String[] words) {
        Map<String, Integer> freqs = new HashMap<>();
        for (var word : words) {
            if (word.isEmpty()) {
                continue;
            }
            freqs.put(word.toLowerCase(), freqs.getOrDefault(word.toLowerCase(), 0) + 1);
        }
        return freqs;
    }

    private List<File> getFilesList (File dirName) {

        List<File> fileList = new ArrayList<>();

        for (File file : dirName.listFiles()) {
            if (file.isFile()) {
                fileList.add(file);
            }
        }
        return fileList;
    }
}