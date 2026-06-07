package ${daoPackageName};

import it.eg.sloth.db.Query;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Dao per la gestione delle sequence
@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ${daoClassName} {

private final JdbcTemplate jdbcTemplate;

    private static final String SQL="SELECT nextval('%s')";

#foreach($sequence in $sequences)
    public Long ${DbUtil.javaObjectName(${sequence.name})}(){
        return jdbcTemplate.queryForObject(SQL.formatted("$sequence.name"),Long.class);
    }
#end

}
