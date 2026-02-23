import java.util.*;

public class EligibilityEngine {
    private final FakeEligibilityStore store;

    public EligibilityEngine(FakeEligibilityStore store) { this.store = store; }

    public void runAndPrint(StudentProfile s) {
        ReportPrinter p = new ReportPrinter();
        EligibilityEngineResult r = evaluate(s); // giant conditional inside
        p.print(s, r);
        store.save(s.rollNo, r.status);
    }

    public EligibilityEngineResult evaluate(StudentProfile s) {
        List<String> reasons = new ArrayList<>();
        String status = "ELIGIBLE";

        // OCP violation: long chain for each rule
        if (s.disciplinaryFlag != LegacyFlags.NONE) {
            status = "NOT_ELIGIBLE";
            reasons.add("disciplinary flag present");
        } else if (s.cgr < 8.0) {
            status = "NOT_ELIGIBLE";
            reasons.add("CGR below 8.0");
        } else if (s.attendancePct < 75) {
            status = "NOT_ELIGIBLE";
            reasons.add("attendance below 75");
        } else if (s.earnedCredits < 20) {
            status = "NOT_ELIGIBLE";
            reasons.add("credits below 20");
        }

        return new EligibilityEngineResult(status, reasons);
    }
}

class EligibilityEngineResult {
    public final String status;
    public final List<String> reasons;
    public EligibilityEngineResult(String status, List<String> reasons) {
        this.status = status;
        this.reasons = reasons;
    }
}
