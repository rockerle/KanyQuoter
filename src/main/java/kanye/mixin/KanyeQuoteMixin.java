package kanye.mixin;

import kanye.Quotes;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ClientPlayerEntity.class)
public class KanyeQuoteMixin {

    @Inject( at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    public void onChatMessage(String message, CallbackInfo ci){

        if(message.equals(".kanye")){
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            Quotes kq = new Quotes("http://api.kanye.rest");
            List<String> pages = new ArrayList<>();

            try {
                for(int i=0;i<50;i++)
                    pages.add(kq.getContent());
            } catch(Exception e){
                ci.cancel();
                return;
            }

            if (player.getInventory().getMainHandStack().getItem() == Items.WRITABLE_BOOK)
                player.networkHandler.sendPacket(new BookUpdateC2SPacket(player.getInventory().selectedSlot, pages, Optional.of("Book of endless wisdom")));

            ci.cancel();
        }
    }
}