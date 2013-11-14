package liquibase.parser.core.sql;

import liquibase.change.core.RawSQLChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.ChangeLogParameters;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.exception.ChangeLogParseException;
import liquibase.parser.ChangeLogParser;
import liquibase.resource.ResourceAccessor;
import liquibase.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;

public class SqlChangeLogParser implements ChangeLogParser {

    public boolean supports(String changeLogFile, ResourceAccessor resourceAccessor) {
        return changeLogFile.endsWith(".sql");
    }

    public int getPriority() {
        return PRIORITY_DEFAULT;
    }
    
    public DatabaseChangeLog parse(String physicalChangeLogLocation, ChangeLogParameters changeLogParameters, ResourceAccessor resourceAccessor) throws ChangeLogParseException {

        DatabaseChangeLog changeLog = new DatabaseChangeLog();
        changeLog.setPhysicalFilePath(physicalChangeLogLocation);

        RawSQLChange change = new RawSQLChange();

        try {
            InputStream sqlStream = resourceAccessor.getResourceAsStream(physicalChangeLogLocation);
            String sql = StreamUtil.getStreamContents(sqlStream, null);
            change.setSql(sql);
            change.setSplitStatements(false);
            change.setEndDelimiter("GO");
        } catch (IOException e) {
            throw new ChangeLogParseException(e);
        }
        change.setResourceAccessor(resourceAccessor);
        change.setSplitStatements(false);
        change.setStripComments(false);

        ChangeSet changeSet = new ChangeSet("raw", "includeAll", false, true, physicalChangeLogLocation, null, null, true, ObjectQuotingStrategy.LEGACY, changeLog);
        changeSet.addChange(change);

        changeLog.addChangeSet(changeSet);

        return changeLog;
    }
}
