package com.duckduckgogogo.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PROJECT_ASSIGNED_TO_SUPPLIER")
public class ProjectSupplierRelation {

    @EmbeddedId
    private Embeddabled id;

    @Column(name = "state")
    public Integer state;

    public Embeddabled getId() {
        return id;
    }

    public void setId(Embeddabled id) {
        this.id = id;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Embeddable
    public static class Embeddabled implements Serializable {
        private long user_id;
        private long project_id;

        public long getUser_id() {
            return user_id;
        }

        public void setUser_id(long user_id) {
            this.user_id = user_id;
        }

        public long getProject_id() {
            return project_id;
        }

        public void setProject_id(long project_id) {
            this.project_id = project_id;
        }
    }
}
