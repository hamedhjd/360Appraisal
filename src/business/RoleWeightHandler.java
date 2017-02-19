package business;

import model.Role;
import model.RoleWeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/9/15
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class RoleWeightHandler {
    public static Map<Role, List<RoleWeight>> roleWeightMap = new HashMap<Role, List<RoleWeight>>(); //key: assessee

    public static void initializeRoleWeights() {
        RoleWeight roleWeight1 = new RoleWeight(Role.UNIT_MANAGER, Role.UNIT_MANAGER, 5);
        RoleWeight roleWeight2 = new RoleWeight(Role.UNIT_MANAGER, Role.CUSTOMER_MANAGER, 5);
        RoleWeight roleWeight3 = new RoleWeight(Role.UNIT_MANAGER, Role.SENIOR_EXPERT, 4);
        RoleWeight roleWeight4 = new RoleWeight(Role.UNIT_MANAGER, Role.EXPERT, 3);
        RoleWeight roleWeight5 = new RoleWeight(Role.UNIT_MANAGER, Role.SECRETARY, 5);

        List<RoleWeight> roleWeights = new ArrayList<RoleWeight>();
        roleWeights.add(roleWeight1);
        roleWeights.add(roleWeight2);
        roleWeights.add(roleWeight3);
        roleWeights.add(roleWeight4);
        roleWeights.add(roleWeight5);
        roleWeightMap.put(roleWeight1.getAssessee(), roleWeights);

        roleWeight1 = new RoleWeight(Role.CUSTOMER_MANAGER, Role.UNIT_MANAGER, 5);
        roleWeight2 = new RoleWeight(Role.CUSTOMER_MANAGER, Role.CUSTOMER_MANAGER, 5);
        roleWeight3 = new RoleWeight(Role.CUSTOMER_MANAGER, Role.SENIOR_EXPERT, 5);
        roleWeight4 = new RoleWeight(Role.CUSTOMER_MANAGER, Role.EXPERT, 4);
        roleWeight5 = new RoleWeight(Role.CUSTOMER_MANAGER, Role.SECRETARY, 5);

        roleWeights = new ArrayList<RoleWeight>();
        roleWeights.add(roleWeight1);
        roleWeights.add(roleWeight2);
        roleWeights.add(roleWeight3);
        roleWeights.add(roleWeight4);
        roleWeights.add(roleWeight5);
        roleWeightMap.put(roleWeight1.getAssessee(), roleWeights);

        roleWeight1 = new RoleWeight(Role.SENIOR_EXPERT, Role.UNIT_MANAGER, 4);
        roleWeight2 = new RoleWeight(Role.SENIOR_EXPERT, Role.CUSTOMER_MANAGER, 4);
        roleWeight3 = new RoleWeight(Role.SENIOR_EXPERT, Role.SENIOR_EXPERT, 5);
        roleWeight4 = new RoleWeight(Role.SENIOR_EXPERT, Role.EXPERT, 5);
        roleWeight5 = new RoleWeight(Role.SENIOR_EXPERT, Role.SECRETARY, 3);

        roleWeights = new ArrayList<RoleWeight>();
        roleWeights.add(roleWeight1);
        roleWeights.add(roleWeight2);
        roleWeights.add(roleWeight3);
        roleWeights.add(roleWeight4);
        roleWeights.add(roleWeight5);
        roleWeightMap.put(roleWeight1.getAssessee(), roleWeights);

        roleWeight1 = new RoleWeight(Role.EXPERT, Role.UNIT_MANAGER, 3);
        roleWeight2 = new RoleWeight(Role.EXPERT, Role.CUSTOMER_MANAGER, 3);
        roleWeight3 = new RoleWeight(Role.EXPERT, Role.SENIOR_EXPERT, 5);
        roleWeight4 = new RoleWeight(Role.EXPERT, Role.EXPERT, 5);
        roleWeight5 = new RoleWeight(Role.EXPERT, Role.SECRETARY, 3);

        roleWeights = new ArrayList<RoleWeight>();
        roleWeights.add(roleWeight1);
        roleWeights.add(roleWeight2);
        roleWeights.add(roleWeight3);
        roleWeights.add(roleWeight4);
        roleWeights.add(roleWeight5);
        roleWeightMap.put(roleWeight1.getAssessee(), roleWeights);

        roleWeight1 = new RoleWeight(Role.SECRETARY, Role.UNIT_MANAGER, 5);
        roleWeight2 = new RoleWeight(Role.SECRETARY, Role.CUSTOMER_MANAGER, 4);
        roleWeight3 = new RoleWeight(Role.SECRETARY, Role.SENIOR_EXPERT, 3);
        roleWeight4 = new RoleWeight(Role.SECRETARY, Role.EXPERT, 3);
        roleWeight5 = new RoleWeight(Role.SECRETARY, Role.SECRETARY, 5);

        roleWeights = new ArrayList<RoleWeight>();
        roleWeights.add(roleWeight1);
        roleWeights.add(roleWeight2);
        roleWeights.add(roleWeight3);
        roleWeights.add(roleWeight4);
        roleWeights.add(roleWeight5);
        roleWeightMap.put(roleWeight1.getAssessee(), roleWeights);
    }

    public static double findWeight(Role assessor, Role assessee) {
        List<RoleWeight> roleWeights = roleWeightMap.get(assessee);
        if (roleWeights == null) {
            return -1;
        }
        for (RoleWeight roleWeight : roleWeights) {
            if (roleWeight.getAssessor() == assessor) {
                return roleWeight.getWeight();
            }
        }
        return -1;
    }

}
