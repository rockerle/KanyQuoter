package kanye.mixin;

import kanye.Quotes;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(ClientPlayerEntity.class)
public abstract class KanyeQuoteMixin {

    @Shadow public abstract void sendSystemMessage(Text message, UUID sender);

    private Quotes kq;

    @Inject(at=@At("TAIL"),method="<init>")
    public void ClientPlayerEntity(CallbackInfo ci){
        kq = new Quotes("http://api.kanye.rest");
    }

    @Inject( at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    public void onChatMessage(String message, CallbackInfo ci){

        if(message.equals(".kanye")){
            kq.getQuotes(50);
            ci.cancel();
            return;
        }
        else if(message.startsWith(".kanye")){
            String m = message.substring(7);
            try {
                int pages = Integer.valueOf(m);
                if(pages<=50 && pages>0)
                    kq.getQuotes(pages);
                else if(pages<0){
                    this.sendSystemMessage(new LiteralText("Oh, so you`re a funny guy?"), null);
                    kq.getQuotes(Math.abs(pages));
                }
                else kq.getQuotes(50);
                ci.cancel();
            } catch(Exception e){
                this.sendSystemMessage(new LiteralText("invalid arguments: .kanye <Number of Quotes max 50>"),null);
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "tick", cancellable = true)
    public void onTick(CallbackInfo ci) {

        if (kq.readyToRead) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            List<String> pages = kq.getPages();

            if (player.getInventory().getMainHandStack().getItem() == Items.WRITABLE_BOOK)
                player.networkHandler.sendPacket(new BookUpdateC2SPacket(player.getInventory().selectedSlot, pages, Optional.of("Book of Endless Wisdom")));

            kq.readyToRead = false;
            kq.emptyPages();
        }
    }
}