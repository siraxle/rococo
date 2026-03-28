package guru.qa.service.logging;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.languages.Dialect;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.StdoutLogger;
import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
import org.apache.commons.lang3.StringUtils;

public class AllureAppender extends StdoutLogger {

    private static final String TEMPLATE_PATH = "sql-attachment.ftl";
    private final AttachmentProcessor<AttachmentData> attachmentProcessor = new DefaultAttachmentProcessor();

    @Override
    public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
        if (StringUtils.isNotEmpty(sql)) {
            try {
                String dbName = extractDatabaseName(url);
                String formattedSql = SqlFormatter.of(Dialect.MySql).format(sql);
                SqlAttachmentData attachmentData = new SqlAttachmentData(
                        sql.split("\\s+")[0].toUpperCase() + " query to: " + dbName,
                        formattedSql
                );
                attachmentProcessor.addAttachment(attachmentData, new FreemarkerAttachmentRenderer(TEMPLATE_PATH));
            } catch (Exception e) {
                System.err.println("Failed to log SQL to Allure: " + e.getMessage());
            }
        }
    }

    private String extractDatabaseName(String url) {
        String afterLastSlash = StringUtils.substringAfterLast(url, "/");
        if (afterLastSlash.contains("?")) {
            afterLastSlash = StringUtils.substringBefore(afterLastSlash, "?");
        }
        if (afterLastSlash.contains(";")) {
            afterLastSlash = StringUtils.substringBefore(afterLastSlash, ";");
        }
        return afterLastSlash;
    }
}