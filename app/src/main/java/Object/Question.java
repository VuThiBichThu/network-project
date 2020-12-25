package Object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Question implements Parcelable, Serializable {
    private String id;
    private String name;
    private String question;
    private Map<Integer, String> answer;
    private Integer correctAnswer;

    public Question() {
    }

    public Question(String question, Map<Integer, String> answer, Integer correctAnswer) {
        this.id = id;
        this.name = name;
        this.question = question;
        this.answer = answer;
        this.correctAnswer = correctAnswer;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<Integer, String> getAnswer() {
        return answer;
    }

    public void setAnswer(Map<Integer, String> answer) {
        this.answer = answer;
    }

    public Integer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public String toString() {
        String xx = "";
        for (Map.Entry entry : answer.entrySet()) {
            xx += entry.getKey() + ": " + entry.getValue() + "\n";
        }
        return "Question{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", question='" + question + '\'' +
                ", answer=" + xx +
                ", correctAnswer=" + correctAnswer +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

}
