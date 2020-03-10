
package af.gov.anar.spm.spm.data;

import java.util.List;

public class QuestionData {

    private Long id;
    private List<ResponseData> responseDatas;
    private String componentKey;
    private String key;
    private String text;
    private String description;
    private Integer sequenceNo;

    public QuestionData() {
        super();
    }

    public QuestionData(final Long id, final List<ResponseData> responseDatas, final String componentKey, final String key,
                        final String text, final String description, final Integer sequenceNo) {
        super();
        this.id = id;
        this.responseDatas = responseDatas;
        this.componentKey = componentKey;
        this.key = key;
        this.text = text;
        this.description = description;
        this.sequenceNo = sequenceNo;
    }

    public Long getId() {
        return id;
    }

    public List<ResponseData> getResponseDatas() {
        return responseDatas;
    }

    public void setResponseDatas(List<ResponseData> responseDatas) {
        this.responseDatas = responseDatas;
    }

    public String getComponentKey() {
        return componentKey;
    }

    public void setComponentKey(String componentKey) {
        this.componentKey = componentKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }
}
