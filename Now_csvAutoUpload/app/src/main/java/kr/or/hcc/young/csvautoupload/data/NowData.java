package kr.or.hcc.young.csvautoupload.data;

public class NowData {

    private String date;
    private String title;
    private String content;
    private String explanation;

    public NowData() {

    }

    public NowData(String date, String title, String content, String explanation) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.explanation = explanation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public String toString() {
        return "NowData{" +
                "date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}
