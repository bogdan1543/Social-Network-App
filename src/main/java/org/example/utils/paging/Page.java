package org.example.utils.paging;

public class Page<E> {
    private Iterable<E> elementsOnPage;
    private int totalNumberElements;

    public Page(Iterable<E> elementsOnPage, int totalNumberElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberElements = totalNumberElements;
    }

    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalNumberElements() {
        return totalNumberElements;
    }


}

