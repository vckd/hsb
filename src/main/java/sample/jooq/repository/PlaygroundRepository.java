package sample.jooq.repository;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sample.jooq.domain.tables.Playground;
import sample.jooq.domain.tables.records.PlaygroundRecord;

import java.util.List;

/**
 * Created by chal on 10/8/15.
 */
@Repository
public class PlaygroundRepository {
    @Autowired
    private DSLContext dsl ;

    public void find(){
        Playground p = Playground.PLAYGROUND.as("p");
        Result<PlaygroundRecord> playgroundList = dsl.selectFrom(p).fetch().into(Playground.PLAYGROUND);
        for(PlaygroundRecord pgr : playgroundList){
            System.out.println(pgr.field1());
        }
    }
}
