import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<Oscar> oscarMale = addOscars(pathMale());
        List<Oscar> oscarFemale = addOscars(pathFemale());

        System.out.println("1 - Quem foi o ator mais jovem a ganhar um Oscar?");
        oscarMale.stream()
                .min((age1, age2) -> age1.getAge() - age2.getAge())
                .map(Oscar::getName)
                .ifPresent(System.out::println);


        System.out.println("\n2 - Quem foi a atriz que mais vezes foi premiada?");
        oscarFemale.stream()
                .map(Oscar::getName)
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(s -> System.out.println(s.getKey() +" com " + s.getValue() + " premiações"));

        
        System.out.println("\n3 - Qual atriz entre 20 e 30 anos que mais vezes foi vencedora?");
        oscarFemale.stream()
                .filter(age -> age.getAge() >=20 && age.getAge() <= 30)
                .map(Oscar::getName)
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(s -> System.out.println(s.getKey() +" com " + s.getValue() + " premiações"));


        List<Oscar> oscars;
        oscars = Stream
                .concat(oscarMale.stream(), oscarFemale.stream())
                .collect(Collectors.toList());

        System.out.println("\n4 - Quais atores ou atrizes receberam mais de um Oscar?");
        oscars.stream()
                .map(Oscar::getName)
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(s -> s.getValue() > 1)
                .forEach(s -> System.out.println(s.getKey() +" com " + s.getValue() + " premiações"));


        System.out.println("\n5 - Selecionar ator");
        selectActor(oscars, "Natalie Portman");
        selectActor(oscars, "Tom Hanks");
        selectActor(oscars, "Katharine Hepburn");


        oscars = oscars.stream()
                .sorted(Comparator.comparing(Oscar::getYear))
                .collect(Collectors.toList());
        exportToFile(oscars);
    }

    private static String getPath() {
        return System.getProperty("user.home") + "/IdeaProjects/arquivos-stream/";
    }

    private static Path pathMale() {
        return Path.of(getPath(), "oscar_age_male.csv");
    }

    private static Path pathFemale() {
        return Path.of(getPath(), "oscar_age_female.csv");
    }

    private static List<String> readFile(Path path) {
        try(Stream<String> file = Files.lines(path)){
            return file
                    .skip(1)
                    .collect(Collectors.toList());
        } catch (IOException e){
            System.err.println("Erro ao ler o arquivo");
        }
        return null;
    }

    private static List<Oscar> addOscars(Path path) {
        List<String> listAward = readFile(path);

        List<Oscar> oscars = new ArrayList<>();
        for(String line : listAward){
            String[] oscar = line.split(";");
            int index = Integer.parseInt(oscar[0]);
            String name = oscar[3].replaceFirst(" ","");
            int age = Integer.parseInt(oscar[2].replace(" ", ""));
            int year = Integer.parseInt(oscar[1].replace(" ", ""));
            String movie = oscar[4].replaceFirst(" ","");
            oscars.add(new Oscar(index, name, age, year, movie));
        }
        return oscars;
    }

    private static void selectActor(List<Oscar> oscars, String name) {
        List<Oscar> selected = oscars.stream()
                .filter(s -> s.getName().equals(name))
                .collect(Collectors.toList());

        System.out.printf("\n%s, %d anos, ganhou %d Oscar no(s) seguinte(s) filme(s):\n",
                selected.get(0).getName(),
                selected.get(0).getAge(),
                selected.size()
        );
        selected.stream()
                .forEach(f -> System.out.println("- " + f.getMovie() + ", Ano: " + f.getYear()));
    }

    private static void exportToFile(List<Oscar> oscars) {
        String path = getPath() + "oscar_age.csv";

        try(FileOutputStream file = new FileOutputStream(path)){
            String linha = "Index; Year; Age; Name; Movie\n";
            file.write(linha.getBytes());
            for(Oscar oscar : oscars){
                linha = oscar.getId() + "; ";
                linha += oscar.getYear() + "; ";
                linha += oscar.getAge() + "; ";
                linha += oscar.getName() + "; ";
                linha += oscar.getMovie() + "\n";
                file.write(linha.getBytes());
            }
        } catch (IOException e){
            System.err.println("Erro ao gravar no arquivo");
        }
    }
}
