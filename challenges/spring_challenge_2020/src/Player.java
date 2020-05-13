import java.util.*;
import java.util.stream.Collectors;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);

        // Build map
        int[][] gameMap = MapBuilder.build(in);

        TurnDataUpdater updater = new TurnDataUpdater(in);
        // game loop
        while (true) {

            // Update Turn Data
            TurnData data = updater.updateData(gameMap);

            // Find Target
            Map<Integer, Position> targets = TargetFinder.findTargets(gameMap, data);

            // Call !
            String command = CommandBuilder.buildCommand(data, targets);
            System.out.println(command);
        }
    }


    static class TurnData {
        int turnNumber = 0;
        int myScore;
        int opponentScore;
        int visiblePacCount; // all your pacs and enemy pacs in sight
        int visiblePelletCount; // all pellets in sight
        Map<Integer, Pac> myPacs = new HashMap<>();
        Map<Integer, Pac> badPacs = new HashMap<>();
        Set<Position> miniPelletPositions = new HashSet<>();
        Set<Position> bigPelletPositions = new HashSet<>();

        public int getTurnNumber() {
            return turnNumber;
        }

        public void setTurnNumber(int turnNumber) {
            this.turnNumber = turnNumber;
        }

        public int getMyScore() {
            return myScore;
        }

        public void setMyScore(int myScore) {
            this.myScore = myScore;
        }

        public int getOpponentScore() {
            return opponentScore;
        }

        public void setOpponentScore(int opponentScore) {
            this.opponentScore = opponentScore;
        }

        public int getVisiblePacCount() {
            return visiblePacCount;
        }

        public void setVisiblePacCount(int visiblePacCount) {
            this.visiblePacCount = visiblePacCount;
        }

        public int getVisiblePelletCount() {
            return visiblePelletCount;
        }

        public void setVisiblePelletCount(int visiblePelletCount) {
            this.visiblePelletCount = visiblePelletCount;
        }

        public Map<Integer, Pac> getMyPacs() {
            return myPacs;
        }

        public void setMyPacs(Map<Integer, Pac> myPacs) {
            this.myPacs = myPacs;
        }

        public Map<Integer, Pac> getBadPacs() {
            return badPacs;
        }

        public void setBadPacs(Map<Integer, Pac> badPacs) {
            this.badPacs = badPacs;
        }

        public Set<Position> getMiniPelletPositions() {
            return miniPelletPositions;
        }

        public void setMiniPelletPositions(Set<Position> miniPelletPositions) {
            this.miniPelletPositions = miniPelletPositions;
        }

        public Set<Position> getBigPelletPositions() {
            return bigPelletPositions;
        }

        public void setBigPelletPositions(Set<Position> bigPelletPositions) {
            this.bigPelletPositions = bigPelletPositions;
        }
    }

    static class Pac {
        int pacId; // pac number (unique within a team)
        boolean mine; // true if this pac is yours
        int x; // position in the grid
        int y; // position in the grid
        int prevX; // previous position in the grid
        int prevY; // previous position in the grid
        String typeId; // unused in wood leagues
        int speedTurnsLeft; // unused in wood leagues
        int abilityCooldown; // unused in wood leagues
        int updatedAtTurn;
        PacBrain brain = new PacBrain();

        Pac(int pacId, boolean mine) {
            this.pacId = pacId;
            this.mine = mine;
        }

        public int getPacId() {
            return pacId;
        }

        public void setPacId(int pacId) {
            this.pacId = pacId;
        }

        public boolean isMine() {
            return mine;
        }

        public void setMine(boolean mine) {
            this.mine = mine;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getPrevX() {
            return prevX;
        }

        public void setPrevX(int prevX) {
            this.prevX = prevX;
        }

        public int getPrevY() {
            return prevY;
        }

        public void setPrevY(int prevY) {
            this.prevY = prevY;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public int getSpeedTurnsLeft() {
            return speedTurnsLeft;
        }

        public void setSpeedTurnsLeft(int speedTurnsLeft) {
            this.speedTurnsLeft = speedTurnsLeft;
        }

        public int getAbilityCooldown() {
            return abilityCooldown;
        }

        public void setAbilityCooldown(int abilityCooldown) {
            this.abilityCooldown = abilityCooldown;
        }

        public int getUpdatedAtTurn() {
            return updatedAtTurn;
        }

        public void setUpdatedAtTurn(int updatedAtTurn) {
            this.updatedAtTurn = updatedAtTurn;
        }

        public PacBrain getBrain() {
            return brain;
        }

        public void setBrain(PacBrain brain) {
            this.brain = brain;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Pac.class.getSimpleName() + "[", "]")
                    .add("pacId=" + pacId)
                    .add("mine=" + mine)
                    .add("x=" + x)
                    .add("y=" + y)
                    .add("typeId='" + typeId + "'")
                    .add("speedTurnsLeft=" + speedTurnsLeft)
                    .add("abilityCooldown=" + abilityCooldown)
                    .add("brain=" + brain)
                    .toString();
        }
    }

    static class PacBrain {
        static final String[] THOUGHTS = {"Itok.", "Zoktok.", "Ur-ur-ur!", "Glor'duk.", "Zug zug.", "Dabu.",
                "Lok'tar.", "Swobu."};
        static final int THOUGHT_FATIGUE = 5;

        int callCounter = 0;
        String currentThought = THOUGHTS[0];

        public String readMyThoughts() {
            if (callCounter > THOUGHT_FATIGUE) {
                currentThought = THOUGHTS[new Random().nextInt(THOUGHTS.length)];
                callCounter = 0;
            } else {
                callCounter++;
            }

            return currentThought;
        }
    }

    static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x &&
                    y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return x + ":" + y;
        }
    }

    static class MapBuilder {
        static int[][] build(Scanner in) {

            int width = in.nextInt(); // size of the grid
            int height = in.nextInt(); // top left corner is (x=0, y=0)

            int[][] map = new int[width][height];

            if (in.hasNextLine()) {
                in.nextLine();
            }
            for (int rn = 0; rn < height; rn++) {
                String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
                char[] chars = row.toCharArray();
                for (int ln = 0; ln < chars.length; ln++) {
                    if ('#' == chars[ln]) {
                        map[ln][rn] = -1;
                    } else {
                        map[ln][rn] = 1;
                    }
                }
            }

            return map;
        }
    }

    static class TurnDataUpdater {

        TurnData data = new TurnData();
        Scanner in;

        TurnDataUpdater(Scanner in) {
            this.in = in;
        }

        TurnData updateData(int[][] map) {

            data.setTurnNumber(data.getTurnNumber() + 1);

            // Update Pac Data
            data.setMyScore(in.nextInt());
            data.setOpponentScore(in.nextInt());

            // Update Pac Data
            data.setVisiblePacCount(in.nextInt()); // all your pacs and enemy pacs in sight

            for (int i = 0; i < data.getVisiblePacCount(); i++) {
                int pacId = in.nextInt();
                boolean mine = in.nextInt() != 0;

                Pac pac;
                if (mine) {
                    pac = Optional.ofNullable(data.getMyPacs().get(pacId)).orElseGet(() -> {
                        data.getMyPacs().put(pacId, new Pac(pacId, true));
                        return data.getMyPacs().get(pacId);
                    });
                } else {
                    pac = Optional.ofNullable(data.getBadPacs().get(pacId)).orElseGet(() -> {
                        data.getBadPacs().put(pacId, new Pac(pacId, false));
                        return data.getBadPacs().get(pacId);
                    });
                }

                pac.setPrevX(pac.getX());
                pac.setPrevY(pac.getY());
                pac.setX(in.nextInt());
                pac.setY(in.nextInt());
                pac.setTypeId(in.next());
                pac.setSpeedTurnsLeft(in.nextInt());
                pac.setAbilityCooldown(in.nextInt());
                pac.setUpdatedAtTurn(data.getTurnNumber());

                if (mine) {
                    resetGameMapSight(pac.getX(), pac.getY(), map);
                }
            }

            if (data.getVisiblePacCount() < data.getMyPacs().size() + data.getBadPacs().size()) {
                removeDeadPacs(data.getMyPacs(), data.getBadPacs());
            }

            // Update Map
            data.setVisiblePelletCount(in.nextInt()); // all pellets in sight
            data.setMiniPelletPositions(new HashSet<>());
            data.setBigPelletPositions(new HashSet<>());
            for (int i = 0; i < data.getVisiblePelletCount(); i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                map[x][y] = in.nextInt(); // amount of points this pellet is worth

                if (map[x][y] == 1) {
                    data.getMiniPelletPositions().add(new Position(x, y));
                } else if (map[x][y] == 10) {
                    data.getBigPelletPositions().add(new Position(x, y));
                }
            }

            return data;
        }

        private void resetGameMapSight(int x, int y, int[][] map) {

            boolean reseted = false;
            boolean leftWalled = false;
            boolean rightWalled = false;
            boolean upWalled = false;
            boolean downWalled = false;

            int step = 0;
            map[x][y] = 0;
            while (!reseted) {
                step++;

                if (!leftWalled && x - step > -1 && map[x - step][y] != -1) {
                    map[x - step][y] = 0;
                } else {
                    leftWalled = true;
                }

                if (!rightWalled && x + step < map.length && map[x + step][y] != -1) {
                    map[x + step][y] = 0;
                } else {
                    rightWalled = true;
                }

                if (!upWalled && y - step > -1 && map[x][y - step] != -1) {
                    map[x][y - step] = 0;
                } else {
                    upWalled = true;
                }

                if (!downWalled && y + step < map[0].length && map[x][y + step] != -1) {
                    map[x][y + step] = 0;
                } else {
                    downWalled = true;
                }

                if (leftWalled && rightWalled && upWalled && downWalled) {
                    reseted = true;
                }
            }
        }

        private void removeDeadPacs(Map<Integer, Pac> myPacs, Map<Integer, Pac> badPacs) {
            myPacs.entrySet().removeIf(entry -> entry.getValue().getUpdatedAtTurn() < data.getTurnNumber());
            badPacs.entrySet().removeIf(entry -> entry.getValue().getUpdatedAtTurn() < data.getTurnNumber());
        }

        String drawMap(int[][] gameMap) {

            StringJoiner drawer = new StringJoiner("\n");
            drawer.add("=~=~=~=~=~=~=~=~=~=~=~=~=~=~");
            for (int[] line : gameMap) {
                StringBuilder sb = new StringBuilder();
                for (int dot : line) {
                    switch (dot) {
                        case -1:
                            sb.append("#");
                            break;
                        case 1:
                            sb.append("o");
                            break;
                        case 10:
                            sb.append("0");
                            break;
                        default:
                            sb.append(" ");
                            break;
                    }
                }
                drawer.add(sb.toString());
            }
            return drawer.toString();
        }
    }

    static class TargetFinder {

        private static final String KEY_SEPARATOR = "-";

        static Map<Integer, Position> findTargets(int[][] gameMap, TurnData data) {

            Set<Position> lockedTargets = new HashSet<>();
            Map<Integer, Position> targets = new HashMap<>();

            distributeBigPellets(data.getMyPacs(), data.getBigPelletPositions(), targets);
            for (Position target : targets.values()) {
                lockedTargets.add(target);
            }

            data.getMyPacs().forEach((pacId, pac) -> {
                if (targets.get(pacId) == null) {
                    Position target = findNearestPellet(pac, data.getMiniPelletPositions(), lockedTargets);
                    if (target == null) {
                        target = findDefaultTarget(gameMap, lockedTargets, pac);
                    }
                    if (target == null) {
                        target = chaseBadPac(pac, data.getBadPacs());
                    }
                    lockedTargets.add(target);
                    targets.put(pacId, target);
                }
            });
            return targets;
        }

        private static Position chaseBadPac(Pac pac, Map<Integer, Pac> badPacs) {

            if (badPacs.get(pac.getPacId()) != null) {
                Pac toChase = badPacs.get(pac.getPacId());
                return new Position(toChase.getX(), toChase.getY());
            } else {
                Iterator<Pac> itPacs = badPacs.values().iterator();
                if (itPacs.hasNext()) {
                    Pac badPac = itPacs.next();
                    return new Position(badPac.getX(), badPac.getY());
                }
            }
            return null;
        }

        private static Position findDefaultTarget(int[][] gameMap, Set<Position> lockedTargets, Pac pac) {
            // Collision
            if (pac.getX() == pac.getPrevX() && pac.getY() == pac.getPrevY()) {
                lockedTargets.add(findFirstFreePellet(gameMap, lockedTargets));
            }
            Position target = findFirstFreePellet(gameMap, lockedTargets);
            if (target != null) {
                lockedTargets.add(target);
            }
            return target;
        }

        private static Position findNearestPellet(Pac pac, Set<Position> pelletPositions, Set<Position> lockedTargets) {

            final int MAX_DEPTH = 4;

            for (int depth = 1; depth < MAX_DEPTH; depth++) {

                for (int x = -depth; x <= depth; x++) {
                    for (int y = -depth; y <= depth; y++) {
                        Position curPos = new Position(pac.getX() + x, pac.getY() + y);
                        if (!lockedTargets.contains(curPos) && pelletPositions.contains(curPos)) {
                            return curPos;
                        }
                    }
                }
            }

            return null;
        }

        private static void distributeBigPellets(Map<Integer, Pac> pacs, Set<Position> bigPelletPositions,
                                                 Map<Integer, Position> targets) {


            Map<String, Integer> distances = new HashMap();

            for (Position position : bigPelletPositions) {
                SortedMap<Integer, Integer> ranks = new TreeMap<>();
                for (Pac pac : pacs.values()) {
                    int distance = calculateFlightDistance(position, new Position(pac.getX(), pac.getY()));
                    distances.put(pac.getPacId() + KEY_SEPARATOR + position.toString(), distance);
                    ranks.put(distance, pac.getPacId());
                }

                Iterator<Integer> itRanks = ranks.values().iterator();
                targetPosition(targets, distances, position, itRanks);
                if (itRanks.hasNext()) {
                    targetPosition(targets, distances, position, itRanks);
                }
            }
        }

        private static void targetPosition(Map<Integer, Position> targets, Map<String, Integer> distances
                , Position position, Iterator<Integer> itRanks) {
            int currentPacId = itRanks.next();
            Position pacTarget = targets.get(currentPacId);
            if (pacTarget == null) {
                targets.put(currentPacId, position);
            } else {
                int currentDistance = distances.get(currentPacId + KEY_SEPARATOR + position.toString());
                int targetDistance = distances.get(currentPacId + KEY_SEPARATOR + pacTarget.toString());
                if (targetDistance > currentDistance) {
                    targets.put(currentPacId, position);
                }
            }
        }

        static Position findFirstFreePellet(int[][] gameMap, Set<Position> lockedTargets) {

            int x = 0;
            int y = 0;
            while (x < gameMap.length) {
                while (y < gameMap[x].length) {
                    if (gameMap[x][y] > 0 && !lockedTargets.contains(new Position(x, y))) {
                        return new Position(x, y);
                    } else {
                        y++;
                    }
                }
                y = 0;
                x++;
            }

            Iterator<Position> it = lockedTargets.iterator();
            if (it.hasNext()) {
                return it.next();
            } else {
                return null;
            }

        }

        static int calculateFlightDistance(Position a, Position b) {
            return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
        }
    }

    static class CommandBuilder {
        private static String buildCommand(TurnData data, Map<Integer, Position> targets) {

            return data.getMyPacs().values().stream().map(pac -> {

                        // Switches

                        // Speeds
                        if (pac.getAbilityCooldown() == 0) {
                            return generateSpeedCommand(data, pac);
                        }

                        // Moves
                        return generateMoveCommand(data, targets, pac);
                    }
            ).collect(Collectors.joining(" | "));
        }

        private static String generateMoveCommand(TurnData data, Map<Integer, Position> targets, Pac pac) {
            return (targets.get(pac.getPacId()) == null)
                    ? ""
                    : new StringJoiner(" ")
                    .add("MOVE")
                    .add(String.valueOf(pac.getPacId()))
                    .add(String.valueOf(targets.get(pac.getPacId()).getX()))
                    .add(String.valueOf(targets.get(pac.getPacId()).getY()))
                    .add(String.valueOf(pac.getPacId())
                            + (data.getTurnNumber() % 2 == 0
                            ? "->" + targets.get(pac.getPacId()).getX() + "/" + targets.get(pac.getPacId()).getY()
                            : ": \"" + pac.getBrain().readMyThoughts() + "\""))
                    .toString();
        }

        private static String generateSpeedCommand(TurnData data, Pac pac) {
            return new StringJoiner(" ")
                    .add("SPEED")
                    .add(String.valueOf(pac.getPacId())).toString();
        }
    }
}