package kanye.mixin;

import kanye.Quotes;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.Optional;

@Mixin(ClientPlayerEntity.class)
public class KanyeQuoteMixin {

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