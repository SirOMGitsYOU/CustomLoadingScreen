package alexiil.mc.mod.load.json.subtypes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import alexiil.mc.mod.load.baked.BakedRender;
import alexiil.mc.mod.load.baked.render.BakedAnimatedRender;
import alexiil.mc.mod.load.baked.render.BakedArea;
import alexiil.mc.mod.load.baked.render.BakedImageRender;
import alexiil.mc.mod.load.json.Area;
import alexiil.mc.mod.load.render.TextureAnimator;
import buildcraft.lib.expression.FunctionContext;
import buildcraft.lib.expression.GenericExpressionCompiler;
import buildcraft.lib.expression.api.IExpressionNode.INodeDouble;
import buildcraft.lib.expression.api.InvalidExpressionException;
import buildcraft.lib.expression.node.value.NodeConstantDouble;
import buildcraft.lib.expression.node.value.NodeVariableDouble;

public class JsonRenderImage extends JsonRenderPositioned {
    public final Area texture;
    public final String frame;

    public JsonRenderImage(JsonRenderImage parent, JsonObject json, JsonDeserializationContext context) {
        super(parent, json, context);
        texture = consolidateArea(json, "texture", context, parent == null ? null : parent.texture);
        frame = overrideObject(json, "frame", context, String.class, parent == null ? null : parent.frame, "0");
    }

    @Override
    protected BakedRender actuallyBake(FunctionContext context) throws InvalidExpressionException {
        NodeVariableDouble varWidth = context.putVariableDouble("elem_width");
        NodeVariableDouble varHeight = context.putVariableDouble("elem_height");
        BakedArea pos = position.bake(context);
        BakedArea tex;
        if (texture == null) {
            NodeConstantDouble zero = NodeConstantDouble.ZERO;
            NodeConstantDouble one = NodeConstantDouble.ONE;
            tex = new BakedArea(zero, zero, one, one);
        } else {
            tex = texture.bake(context);
        }
        if (TextureAnimator.isAnimated(resourceLocation.toString())) {
            INodeDouble _frame = GenericExpressionCompiler.compileExpressionDouble(frame, context);
            return new BakedAnimatedRender(varWidth, varHeight, image, pos, tex, _frame);
        } else {
            return new BakedImageRender(varWidth, varHeight, image, pos, tex);
        }
    }
}
