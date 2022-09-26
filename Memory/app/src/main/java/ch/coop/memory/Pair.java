package ch.coop.memory;

public class Pair {
    private Word firstWord;
    private Word secondWord;

    public Word getFirstWord() {
        return firstWord;
    }

    public void setFirstWord(Word firstWord) {
        this.firstWord = firstWord;
    }

    public Word getSecondWord() {
        return secondWord;
    }

    public void setSecondWord(Word secondWord) {
        this.secondWord = secondWord;
    }

    public boolean isFull() {
        if (secondWord != null) {
            return true;
        } else {
            return false;
        }
    }
}
