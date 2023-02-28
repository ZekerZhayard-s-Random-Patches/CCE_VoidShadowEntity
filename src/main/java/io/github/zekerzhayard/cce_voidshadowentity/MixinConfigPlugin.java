package io.github.zekerzhayard.cce_voidshadowentity;

import java.util.List;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
        for (MethodNode mn : targetClass.methods) {
            if (mn.name.equals(resolver.mapMethodName("intermediary", "net.minecraft.class_1309", "method_6108", "()V")) && mn.desc.equals("()V")) {
                boolean isTarget = false;
                int var = -1;
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == Opcodes.CHECKCAST && ((TypeInsnNode) ain).desc.equals("net/voidz/block/entity/PortalBlockEntity")) {
                        isTarget = true;
                        mn.instructions.remove(ain);
                    } else if (isTarget && ain.getOpcode() == Opcodes.ASTORE) {
                        var = ((VarInsnNode) ain).var;
                    } else if (isTarget && ain.getOpcode() == Opcodes.IFNULL) {
                        isTarget = false;
                        InsnList il = new InsnList();
                        il.add(new VarInsnNode(Opcodes.ALOAD, var));
                        il.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/voidz/block/entity/PortalBlockEntity"));
                        il.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) ain).label));
                        il.add(new VarInsnNode(Opcodes.ALOAD, var));
                        il.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/voidz/block/entity/PortalBlockEntity"));
                        il.add(new VarInsnNode(Opcodes.ASTORE, var));
                        mn.instructions.insert(ain, il);
                    }
                }
            }
        }
    }
}
