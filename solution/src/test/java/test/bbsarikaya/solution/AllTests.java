package test.bbsarikaya.solution;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ GasStationImplTest.class, TransactionStatsTest.class, GasStationImplConcurrencyTest.class })
public class AllTests {

}
