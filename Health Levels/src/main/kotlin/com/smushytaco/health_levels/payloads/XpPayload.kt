package com.smushytaco.health_levels.payloads
import com.smushytaco.health_levels.abstractions.HealthMethods
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
class XpPayload(val value: Int) : CustomPayload {
    override fun getId() = payloadId
    companion object {
        val payloadId = CustomPayload.Id<XpPayload>(HealthMethods.HEALTH_XP_PACKET_IDENTIFIER)
        val CODEC: PacketCodec<RegistryByteBuf, XpPayload> = PacketCodec.tuple(PacketCodecs.INTEGER, XpPayload::value) { XpPayload(it) }
    }
}