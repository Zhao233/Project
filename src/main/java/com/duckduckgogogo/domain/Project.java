package com.duckduckgogogo.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "PROJECTS")
public class Project {

    public final static String NOT_ASSIGN = "Not Assign"; // 为分配
    public final static String ASSIGNED = "Assigned"; // 已分配
    public final static String IN_PROGRESS = "In Progress"; // 进行中
    public final static String PENDING = "Pending"; // 待确认
    public final static String COMPLETED = "Completed"; // 完成

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 项目名称，数据来自World Server接口
     */
    @Column(name = "NAME", length = 255)
    private String name;
    /**
     * World Server中Project的id，数据来自World Server接口
     */
    @Column(name = "WORLD_ID", length = 100)
    private String worldId;
    /**
     * 要翻译的语言，数据来自World Server接口
     */
    @Column(name = "LOCALE", length = 50)
    private String locale;
    /**
     * 项目创建时间
     */
    @Column(name = "CREATION_DATE")
    private Date creation;
    /**
     * 项目分派时间
     */
    @Column(name = "ASSINGED_DATE")
    private Date assinged;
    /**
     * 项目提交时间
     */
    @Column(name = "CHECKIN_DATE")
    private Date checkin;
    /**
     * 项目返回World Server时间
     */
    @Column(name = "PUSHBACK_DATE")
    private Date pushback;
    /**
     * 项目经理（谁分配）
     */
    // private long assignedBy;
    @OneToOne
    @JoinColumn(name="ASSIGNED_BY")
    private User projectManager;
    /**
     * 供应商（分配给谁）
     */
    // private long assignedTo;
    @OneToOne
    @JoinColumn(name="ASSIGNED_TO")
    private User supplier;

    @OneToMany
    @JoinTable(name = "PROJECT_ASSIGNED_TO_SUPPLIER",
            joinColumns = {@JoinColumn(name = "PROJECT_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private Set<User> suppliers;
    /**
     * 项目状态（1,Not Assign;2,Assigned;3,In Progress;4,Pending;5,Completed）
     */
    @Column(name = "STATE", length = 30)
    private String state;
    /**
     * 源文件路径
     */
    @Column(name = "SOURCE_FILE_URL", length = 500)
    private String sourceFileUrl;
    /**
     * 源文件名
     */
    @Column(name = "SOURCE_NAME", length = 255)
    private String sourceName;
    /**
     * 翻译后文件路径
     */
    @Column(name = "TRANSLATE_FILE_URL", length = 500)
    private String translateFileUrl;

    /**
     * 翻译后文件名
     */
    @Column(name = "TRANSLATE_NAME", length = 255)
    private String translateName;

    /**
     * 有效性
     */
    @Column(name = "VALID")
    private boolean valid;

    /**
     * 数据版本
     */
    @Version
    @Column(name = "version")
    private int version;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null) this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorldId() {
        return worldId;
    }

    public void setWorldId(String worldId) {
        this.worldId = worldId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Date getAssinged() {
        return assinged;
    }

    public void setAssinged(Date assinged) {
        this.assinged = assinged;
    }

    public Date getCheckin() {
        return checkin;
    }

    public void setCheckin(Date checkin) {
        this.checkin = checkin;
    }

    public Date getPushback() {
        return pushback;
    }

    public void setPushback(Date pushback) {
        this.pushback = pushback;
    }

    public User getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(User projectManager) {
        this.projectManager = projectManager;
    }

    public String getProjectManagerName() {
        return this.projectManager != null ? this.projectManager.getFirstName() + " " + this.projectManager.getLastName() : "";
    }

    public User getSupplier() {
        return supplier;
    }

    public void setSupplier(User supplier) {
        this.supplier = supplier;
    }

    public String getSupplierName() {
        String supplierName = "";
        if (this.supplier != null) {
            supplierName = this.supplier.getFirstName() + " " + this.supplier.getLastName();
        } else if (this.suppliers != null) {
            for (User user : this.suppliers) {
                if (!supplierName.isEmpty()) supplierName += "<br/>";
                supplierName += user.getFirstName() + " " + user.getLastName();
            }
        }

        return supplierName;
    }

    public Set<User> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Set<User> suppliers) {
        this.suppliers = suppliers;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSourceFileUrl() {
        return sourceFileUrl;
    }

    public void setSourceFileUrl(String sourceFileUrl) {
        this.sourceFileUrl = sourceFileUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTranslateFileUrl() {
        return translateFileUrl;
    }

    public void setTranslateFileUrl(String translateFileUrl) {
        this.translateFileUrl = translateFileUrl;
    }

    public String getTranslateName() {
        return translateName;
    }

    public void setTranslateName(String translateName) {
        this.translateName = translateName;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        if (version != null) this.version = version;
    }
}
