package nl.hu.cisq1.lingo.words.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "words")
@NoArgsConstructor
@Getter
public class Word {
    @Id
    @Column(name = "word")
    private String value;
    private Integer length;

    public Word(String word) {
        this.value = word;
        this.length = word.length();
    }
}
