package train;

import java.io.File;
import java.nio.file.Paths;

public enum TrainFiles {
    num(Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\num").toFile()),
    maj(Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\maj").toFile()),
    min(Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\min").toFile()),
    ponct(Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\ponct").toFile()),
    min_acc(Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\min_acc").toFile());

private File file;

private TrainFiles(File f){
    this.file = f;
}

public File getFile(){
    return this.file;
}
}
