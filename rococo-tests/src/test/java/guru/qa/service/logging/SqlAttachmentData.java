package guru.qa.service.logging;

import io.qameta.allure.attachment.AttachmentData;

public class SqlAttachmentData implements AttachmentData {
    private final String name;
    private final String sql;

    public SqlAttachmentData(String name, String sql) {
        this.name = name;
        this.sql = sql;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSql() {
        return sql;
    }
}