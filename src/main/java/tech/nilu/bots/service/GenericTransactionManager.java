package tech.nilu.bots.service;


import tech.nilu.bots.ActionCodes;
import tech.nilu.bots.exception.ApplicationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

/**
 * Created by mmariameda on 12/18/16.
 */
@Component
public class GenericTransactionManager {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T doInSeparateTx(Callable<T> r) {
        try {
            return r.call();
        } catch (ApplicationException be) {
            throw be;
        } catch (Exception e) {
            throw new ApplicationException(500, ActionCodes.UNKNOWN_ERROR, e);
        }
    }
}
