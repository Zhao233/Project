package com.duckduckgogogo.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "AZURE_FILE_HISTORY")
public class AzureFileHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "PROJECT_ID")
    private long projectId;

    @Column(name = "FILE_NAME", length = 255)
    private String fileName;

    @Column(name = "FILE_PATH", length = 500)
    private String filePath;

    @Column(name = "CREATE_BY_USER_ID")
    private long createByUser;

    @Column(name = "CREATE_DATE")
    private Date create;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getCreateByUser() {
        return createByUser;
    }

    public void setCreateByUser(long createByUser) {
        this.createByUser = createByUser;
    }

    public Date getCreate() {
        return create;
    }

    public void setCreate(Date create) {
        this.create = create;
    }
}
