
package af.gov.anar.spm.spm.data;

public class ComponentData {

    private Long id;
    private String key;
    private String text;
    private String description;
    private Integer sequenceNo;

    public ComponentData() {
        super();
    }

    public ComponentData(final Long id, final String key, final String text,
                         final String description, final Integer sequenceNo) {
        super();
        this.id = id;
        this.key = key;
        this.text = text;
        this.description = description;
        this.sequenceNo = sequenceNo;
    }

    public Long getId() {
        return id;
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

    public void setText(String title) {
        this.text = title;
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
