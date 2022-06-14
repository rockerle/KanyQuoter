package kanye.mixin;

import kanye.Quotes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.Optional;

@Mixin(ClientPlayerEntity.class)
public class KanyeQuoteMixin {

    private Quotes kq;

    @Inject(at=@At("TAIL"),method="Lnet/minecraft/client/network/ClientPlayerEntity;<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)V")
    public void ClientPlayerEntity(CallbackInfo ci){
        kq = new Quotes("https://api.kanye.rest");
    }

    @Inject(at=@At("HEAD"), method="Lnet/minecraft/client/network/ClientPlayerEntity;useBook(Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)V", cancellable = true)
    public void useBook(ItemStack book, Hand hand, CallbackInfo ci){
        if(kq.stopBookUse) ci.cancel();
    }
    @Inject( at = @At("HEAD"), method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", cancellable = true)
    public void onChatMessage(String message,Text preview, CallbackInfo ci){
        System.out.println("sending chat message");
        System.out.println(message);
        if(message.equals(".kanye")){
            kq.stopBookUse = true;
            kq.getQuotes(50);
            ci.cancel();
            return;
        } else if(message.startsWith(".kanye")){
            kq.stopBookUse = true;
            String m = message.substring(7);
            try {
                int pages = Integer.valueOf(m);
                if(pages<=50 && pages>0)
                    kq.getQuotes(pages);
                else if(pages<0){
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Oh, so you`re a funny guy?"),true);
                    kq.getQuotes(Math.abs(pages));
                }
                else kq.getQuotes(50);
                ci.cancel();
            } catch(Exception e){
                MinecraftClient.getInstance().player.sendMessage(Text.of("invalid arguments: .kanye <Number of Quotes max 50>"),true);
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void onTick(CallbackInfo ci) {

        if (kq.readyToRead) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            List<String> pages = kq.getPages();
            if (player.getInventory().getMainHandStack().getItem() == Items.WRITABLE_BOOK)
                player.networkHandler.sendPacket(new BookUpdateC2SPacket(player.getInventory().selectedSlot, pages, Optional.of("Book of Endless Wisdom")));
            kq.readyToRead = false;
            kq.stopBookUse = false;
            kq.emptyPages();
        }
    }
}