package kanye.mixin;

import kanye.KanyeQuoter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(at=@At("HEAD"), method="sendChatMessage", cancellable = true)
    public void onChatMessage(String message, CallbackInfo ci){
        System.out.println("sending chat message");
        System.out.println(message);
        if(message.equals(".kanye")){
            KanyeQuoter.kq.stopBookUse = true;
            KanyeQuoter.kq.getQuotes(50);
            ci.cancel();
            return;
        } else if(message.startsWith(".kanye")){
            KanyeQuoter.kq.stopBookUse = true;
            String m = message.substring(7);
            try {
                int pages = Integer.valueOf(m);
                if(pages<=50 && pages>0)
                    KanyeQuoter.kq.getQuotes(pages);
                else if(pages<0){
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Oh, so you`re a funny guy?"),true);
                    KanyeQuoter.kq.getQuotes(Math.abs(pages));
                }
                else KanyeQuoter.kq.getQuotes(50);
                ci.cancel();
            } catch(Exception e){
                MinecraftClient.getInstance().player.sendMessage(Text.of("invalid arguments: .kanye <Number of Quotes max 50>"),true);
                ci.cancel();
            }
        }
    }
}