package com.ericc.the.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

public class RenderableComponent implements Component {
    public Model model;
    public TextureRegion region;
    public Affine2 transform = new Affine2();
    public float brightness = 0.0f;
    public float alpha = 1.0f;
    public float saturation = 1.0f;
    public boolean visible = false;
    public int zOrder;

    public RenderableComponent(Model model, int zOrder) {
        this.model = model;
        this.region = model.sheet[0];
        this.zOrder = zOrder;
    }

    public RenderableComponent(Model model) {
        this(model, 0);
    }
}
