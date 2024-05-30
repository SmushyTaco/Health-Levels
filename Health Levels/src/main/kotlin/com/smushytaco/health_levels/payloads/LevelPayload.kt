package com.smushytaco.health_levels.payloads
import com.smushytaco.health_levels.abstractions.HealthMethods
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
class LevelPayload(val value: Int) : CustomPayload {
    override fun getId() = payloadId
    companion object {
        val payloadId = CustomPayload.Id<LevelPayload>(HealthMethods.HEALTH_LEVEL_PACKET_IDENTIFIER)
        val CODEC: PacketCodec<RegistryByteBuf, LevelPayload> = PacketCodec.tuple(PacketCodecs.INTEGER, LevelPayload::value) { LevelPayload(it) }
    }
}