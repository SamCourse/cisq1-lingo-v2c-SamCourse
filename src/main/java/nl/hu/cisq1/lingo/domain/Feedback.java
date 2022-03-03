package nl.hu.cisq1.lingo.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Feedback {
    private String attempt;
    private List<Mark> marks;

    public boolean isWordGuessed() {
        return marks.stream().allMatch(mark -> mark.equals(Mark.CORRECT));
    }

    public boolean isGuessValid() {
        return marks.stream().noneMatch(mark -> mark.equals(Mark.INVALID));
    }
}
