package com.smushytaco.health_levels.payloads
import com.smushytaco.health_levels.abstractions.HealthMethods
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
class XpPayload(val value: Int) : CustomPacketPayload {
    override fun type() = payloadId
    companion object {
        val payloadId = CustomPacketPayload.Type<XpPayload>(HealthMethods.HEALTH_XP_PACKET_IDENTIFIER)
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, XpPayload> = StreamCodec.composite(ByteBufCodecs.INT, XpPayload::value) { XpPayload(it) }
    }
}