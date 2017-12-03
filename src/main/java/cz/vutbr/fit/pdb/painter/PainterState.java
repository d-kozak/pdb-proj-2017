package cz.vutbr.fit.pdb.painter;

public interface PainterState {
    void clicked(double x, double y);

    void drawingFinished();
}
