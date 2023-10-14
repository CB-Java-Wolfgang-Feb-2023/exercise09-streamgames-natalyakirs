package games.app;

import games.model.Game;
import games.model.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Games {

    private static final Path CSV = Paths.get("Games/games.csv");
    private static final String BUNDESLIGA = "BUNDESLIGA";
    private static final String BAYERN = "FC Bayern Muenchen";

    public static void main(String[] args) throws IOException {

        List<Game> games = null;
        try (Stream<String> lines = Files.lines(CSV)) {
            games = lines.skip(1).map(Game::fromString).collect(toList());
        } catch (Exception e) {
            throw new NoSuchElementException("No CSV file found");
        }

        games.forEach(System.out::println);
        System.out.println();

        // -------------------

        // TODO: How many games are Bundesliga games?
        // (Lösung mit filter)

        long bundesligaGameCount = games.stream()
                .filter(game -> game.getInfo().contains(BUNDESLIGA))
                .count();

        System.out.println("There were " + bundesligaGameCount + " Bundesliga games");
        System.out.println();

        // -------------------


        // TODO: Which games are away and which are home?
        // (Lösung mit partitionBy)



        Map<Boolean, List<Game>> homeAwayMap = games.stream()
                .collect(Collectors.partitioningBy((Game::isHomeGame)))    ;


        System.out.println("*** HOME ***");
        homeAwayMap.get(true).forEach(System.out::println);
        System.out.println("*** AWAY ***");
        homeAwayMap.get(false).forEach(System.out::println);
        System.out.println();

        // -------------------

        // TODO Group the games into won, lost and draw (draw = draw)
        // (Lösung mit groupingBy)

        Map<Result, List<Game>> wonLostDrawMap = games.stream()
                .collect(Collectors.groupingBy(Game::getResult));


        System.out.println("*** WON ***");
        wonLostDrawMap.get(Result.WON).forEach(System.out::println);
        System.out.println("*** DRAW ***");
        wonLostDrawMap.get(Result.DRAW).forEach(System.out::println);
        System.out.println("*** LOST ***");
        wonLostDrawMap.get(Result.LOST).forEach(System.out::println);
        System.out.println();

        // -------------------

        // TODO How many goals were scored on average per game? mapToInt
        // (Lösung mit mapToInt) -> mapToInt operation to map each game to the total number of goals (sum of home and away goals)
        double avgGoalsPerGame1 = games.stream()
                .mapToInt(game -> game.goalCount())
                .average()
                .orElse(0.0);

        System.out.printf("Average goals per game: %.2f\n", avgGoalsPerGame1);


        // TODO How many goals were scored on average per game? averagingDouble
        // (Lösung mit withCollectors.averagingDouble)
        double avgGoalsPerGame2 = games.stream()
                .collect(Collectors.averagingDouble(game -> game.goalCount()));

        System.out.printf("Average goals per game: %.2f\n", avgGoalsPerGame2);
        System.out.println();

        // -------------------

        // TODO How many games has Bayern Munich won at home?
        // (home equals BAYERN)?
        // (Lösung mit double filter und count)
        long wonHomeGamesCount = games.stream()
                .filter(game -> game.isHomeGame() && game.getHome().equals(BAYERN) && game.getResult() == Result.WON)
                .count();


        System.out.println(BAYERN + " won " + wonHomeGamesCount + " games at home");
        System.out.println();

        // -------------------

        // TODO What was the game with the fewest goals? sorted findFirst
        // (Lösung mit sorted und findFirst)
        Game leastNumberOfGoalsGame1 = games.stream()
                .sorted(Comparator.comparingInt(game -> game.goalCount()))
                .findFirst()
                .orElse(null);

        System.out.println("Game with least number of goals: " + leastNumberOfGoalsGame1);

        // TODO What was the game with the fewest goals? min Comparator.comparingInt
        // (Lösung mit min und Comparator.comparingInt)
        Game leastNumberOfGoalsGame2 = games.stream()
                .min(Comparator.comparingInt(game -> game.goalCount()))
                .orElse(null);

        System.out.println("Game with least number of goals: " + leastNumberOfGoalsGame2);
        System.out.println();

        // -------------------


        // TODO What are the different (distinct) start times??
        // (Lösung mit einem stream und Collectors.joining)
        String startingTimesString = games.stream()
                .map(Game::getTime)
                .distinct()
                .collect(Collectors.joining(", "));

        System.out.println("Distinct starting times: " + startingTimesString);
        System.out.println();

        // -------------------


        // TODO has Bayern won an away match with at least 2 goals difference?
        // (home equals BAYERN)?
        // (Lösung mit anyMatch)

        boolean bayernWon = games.stream()
                .filter(game -> !game.isHomeGame()) // Select away games
                .filter(game -> game.getAway().equals("FC Bayern Muenchen")) // Select Bayern Munich (away)
                .anyMatch(game -> (game.getAwayGoals() - game.getHomeGoals()) >= 2);

        System.out.println("Bayern won away game with at least 2 goals difference: " + (bayernWon ? "yes" : "no"));
        System.out.println();

        // -------------------

        // TODO A friend of yours gave you the 2019 games, but they were grouped by home team. But you want to retrieve all the games as a simple list!
        // (Lösung with flatMap und Collectors.toList)
        Map<String, List<Game>> games2019ByHomeTeam = games.stream()
                .filter(game -> game.getDate().contains("2019"))
                .collect(Collectors.groupingBy(Game::getHome));
        List<Game> flattenedGames = games2019ByHomeTeam.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        flattenedGames.forEach(System.out::println);
    }


}
