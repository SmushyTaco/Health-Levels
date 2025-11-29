package com.smushytaco.health_levels.payloads
import com.smushytaco.health_levels.abstractions.HealthMethods
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
class LevelPayload(val value: Int) : CustomPacketPayload {
    override fun type() = payloadId
    companion object {
        val payloadId = CustomPacketPayload.Type<LevelPayload>(HealthMethods.HEALTH_LEVEL_PACKET_IDENTIFIER)
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, LevelPayload> = StreamCodec.composite(ByteBufCodecs.INT, LevelPayload::value) { LevelPayload(it) }
    }
}