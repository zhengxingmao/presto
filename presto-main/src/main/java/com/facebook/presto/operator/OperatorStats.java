/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.operator;

import com.facebook.drift.annotations.ThriftConstructor;
import com.facebook.drift.annotations.ThriftField;
import com.facebook.drift.annotations.ThriftStruct;
import com.facebook.presto.common.RuntimeStats;
import com.facebook.presto.spi.plan.PlanNodeId;
import com.facebook.presto.util.Mergeable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.airlift.units.DataSize;
import io.airlift.units.Duration;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static io.airlift.units.DataSize.succinctBytes;
import static io.airlift.units.Duration.succinctNanos;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Immutable
@ThriftStruct
public class OperatorStats
{
    private final int stageId;
    private final int stageExecutionId;
    private final int pipelineId;
    private final int operatorId;
    private final PlanNodeId planNodeId;
    private final String operatorType;

    private final long totalDrivers;

    private final long addInputCalls;
    private final Duration addInputWall;
    private final Duration addInputCpu;
    private final DataSize addInputAllocation;
    private final DataSize rawInputDataSize;
    private final long rawInputPositions;
    private final DataSize inputDataSize;
    private final long inputPositions;
    private final double sumSquaredInputPositions;

    private final long getOutputCalls;
    private final Duration getOutputWall;
    private final Duration getOutputCpu;
    private final DataSize getOutputAllocation;
    private final DataSize outputDataSize;
    private final long outputPositions;

    private final DataSize physicalWrittenDataSize;

    private final Duration additionalCpu;
    private final Duration blockedWall;

    private final long finishCalls;
    private final Duration finishWall;
    private final Duration finishCpu;
    private final DataSize finishAllocation;

    private final DataSize userMemoryReservation;
    private final DataSize revocableMemoryReservation;
    private final DataSize systemMemoryReservation;
    private final DataSize peakUserMemoryReservation;
    private final DataSize peakSystemMemoryReservation;
    private final DataSize peakTotalMemoryReservation;

    private final DataSize spilledDataSize;

    private final Optional<BlockedReason> blockedReason;

    @Nullable
    private final OperatorInfo info;
    @Nullable
    private final OperatorInfoUnion infoUnion;

    private final RuntimeStats runtimeStats;

    @JsonCreator
    public OperatorStats(
            @JsonProperty("stageId") int stageId,
            @JsonProperty("stageExecutionId") int stageExecutionId,
            @JsonProperty("pipelineId") int pipelineId,
            @JsonProperty("operatorId") int operatorId,
            @JsonProperty("planNodeId") PlanNodeId planNodeId,
            @JsonProperty("operatorType") String operatorType,

            @JsonProperty("totalDrivers") long totalDrivers,

            @JsonProperty("addInputCalls") long addInputCalls,
            @JsonProperty("addInputWall") Duration addInputWall,
            @JsonProperty("addInputCpu") Duration addInputCpu,
            @JsonProperty("addInputAllocation") DataSize addInputAllocation,
            @JsonProperty("rawInputDataSize") DataSize rawInputDataSize,
            @JsonProperty("rawInputPositions") long rawInputPositions,
            @JsonProperty("inputDataSize") DataSize inputDataSize,
            @JsonProperty("inputPositions") long inputPositions,
            @JsonProperty("sumSquaredInputPositions") double sumSquaredInputPositions,

            @JsonProperty("getOutputCalls") long getOutputCalls,
            @JsonProperty("getOutputWall") Duration getOutputWall,
            @JsonProperty("getOutputCpu") Duration getOutputCpu,
            @JsonProperty("getOutputAllocation") DataSize getOutputAllocation,
            @JsonProperty("outputDataSize") DataSize outputDataSize,
            @JsonProperty("outputPositions") long outputPositions,

            @JsonProperty("physicalWrittenDataSize") DataSize physicalWrittenDataSize,

            @JsonProperty("additionalCpu") Duration additionalCpu,
            @JsonProperty("blockedWall") Duration blockedWall,

            @JsonProperty("finishCalls") long finishCalls,
            @JsonProperty("finishWall") Duration finishWall,
            @JsonProperty("finishCpu") Duration finishCpu,
            @JsonProperty("finishAllocation") DataSize finishAllocation,

            @JsonProperty("userMemoryReservation") DataSize userMemoryReservation,
            @JsonProperty("revocableMemoryReservation") DataSize revocableMemoryReservation,
            @JsonProperty("systemMemoryReservation") DataSize systemMemoryReservation,
            @JsonProperty("peakUserMemoryReservation") DataSize peakUserMemoryReservation,
            @JsonProperty("peakSystemMemoryReservation") DataSize peakSystemMemoryReservation,
            @JsonProperty("peakTotalMemoryReservation") DataSize peakTotalMemoryReservation,

            @JsonProperty("spilledDataSize") DataSize spilledDataSize,

            @JsonProperty("blockedReason") Optional<BlockedReason> blockedReason,

            @Nullable
            @JsonProperty("info") OperatorInfo info,
            @JsonProperty("runtimeStats") RuntimeStats runtimeStats)
    {
        this.stageId = stageId;
        this.stageExecutionId = stageExecutionId;
        this.pipelineId = pipelineId;

        checkArgument(operatorId >= 0, "operatorId is negative");
        this.operatorId = operatorId;
        this.planNodeId = requireNonNull(planNodeId, "planNodeId is null");
        this.operatorType = requireNonNull(operatorType, "operatorType is null");

        this.totalDrivers = totalDrivers;

        this.addInputCalls = addInputCalls;
        this.addInputWall = requireNonNull(addInputWall, "addInputWall is null");
        this.addInputCpu = requireNonNull(addInputCpu, "addInputCpu is null");
        this.addInputAllocation = requireNonNull(addInputAllocation, "addInputAllocation is null");
        this.rawInputDataSize = requireNonNull(rawInputDataSize, "rawInputDataSize is null");
        this.rawInputPositions = requireNonNull(rawInputPositions, "rawInputPositions is null");
        this.inputDataSize = requireNonNull(inputDataSize, "inputDataSize is null");
        checkArgument(inputPositions >= 0, "inputPositions is negative");
        this.inputPositions = inputPositions;
        this.sumSquaredInputPositions = sumSquaredInputPositions;

        this.getOutputCalls = getOutputCalls;
        this.getOutputWall = requireNonNull(getOutputWall, "getOutputWall is null");
        this.getOutputCpu = requireNonNull(getOutputCpu, "getOutputCpu is null");
        this.getOutputAllocation = requireNonNull(getOutputAllocation, "getOutputAllocation is null");
        this.outputDataSize = requireNonNull(outputDataSize, "outputDataSize is null");
        checkArgument(outputPositions >= 0, "outputPositions is negative");
        this.outputPositions = outputPositions;

        this.physicalWrittenDataSize = requireNonNull(physicalWrittenDataSize, "writtenDataSize is null");

        this.additionalCpu = requireNonNull(additionalCpu, "additionalCpu is null");
        this.blockedWall = requireNonNull(blockedWall, "blockedWall is null");

        this.finishCalls = finishCalls;
        this.finishWall = requireNonNull(finishWall, "finishWall is null");
        this.finishCpu = requireNonNull(finishCpu, "finishCpu is null");
        this.finishAllocation = requireNonNull(finishAllocation, "finishAllocation is null");

        this.userMemoryReservation = requireNonNull(userMemoryReservation, "userMemoryReservation is null");
        this.revocableMemoryReservation = requireNonNull(revocableMemoryReservation, "revocableMemoryReservation is null");
        this.systemMemoryReservation = requireNonNull(systemMemoryReservation, "systemMemoryReservation is null");

        this.peakUserMemoryReservation = requireNonNull(peakUserMemoryReservation, "peakUserMemoryReservation is null");
        this.peakSystemMemoryReservation = requireNonNull(peakSystemMemoryReservation, "peakSystemMemoryReservation is null");
        this.peakTotalMemoryReservation = requireNonNull(peakTotalMemoryReservation, "peakTotalMemoryReservation is null");

        this.spilledDataSize = requireNonNull(spilledDataSize, "spilledDataSize is null");

        this.runtimeStats = runtimeStats;

        this.blockedReason = blockedReason;

        this.info = info;
        this.infoUnion = null;
    }

    @ThriftConstructor
    public OperatorStats(
            int stageId,
            int stageExecutionId,
            int pipelineId,
            int operatorId,
            PlanNodeId planNodeId,
            String operatorType,

            long totalDrivers,

            long addInputCalls,
            Duration addInputWall,
            Duration addInputCpu,
            DataSize addInputAllocation,
            DataSize rawInputDataSize,
            long rawInputPositions,
            DataSize inputDataSize,
            long inputPositions,
            double sumSquaredInputPositions,

            long getOutputCalls,
            Duration getOutputWall,
            Duration getOutputCpu,
            DataSize getOutputAllocation,
            DataSize outputDataSize,
            long outputPositions,

            DataSize physicalWrittenDataSize,

            Duration additionalCpu,
            Duration blockedWall,

            long finishCalls,
            Duration finishWall,
            Duration finishCpu,
            DataSize finishAllocation,

            DataSize userMemoryReservation,
            DataSize revocableMemoryReservation,
            DataSize systemMemoryReservation,
            DataSize peakUserMemoryReservation,
            DataSize peakSystemMemoryReservation,
            DataSize peakTotalMemoryReservation,

            DataSize spilledDataSize,

            Optional<BlockedReason> blockedReason,

            RuntimeStats runtimeStats,
            @Nullable
            OperatorInfoUnion infoUnion)
    {
        this.stageId = stageId;
        this.stageExecutionId = stageExecutionId;
        this.pipelineId = pipelineId;

        checkArgument(operatorId >= 0, "operatorId is negative");
        this.operatorId = operatorId;
        this.planNodeId = requireNonNull(planNodeId, "planNodeId is null");
        this.operatorType = requireNonNull(operatorType, "operatorType is null");

        this.totalDrivers = totalDrivers;

        this.addInputCalls = addInputCalls;
        this.addInputWall = requireNonNull(addInputWall, "addInputWall is null");
        this.addInputCpu = requireNonNull(addInputCpu, "addInputCpu is null");
        this.addInputAllocation = requireNonNull(addInputAllocation, "addInputAllocation is null");
        this.rawInputDataSize = requireNonNull(rawInputDataSize, "rawInputDataSize is null");
        this.rawInputPositions = requireNonNull(rawInputPositions, "rawInputPositions is null");
        this.inputDataSize = requireNonNull(inputDataSize, "inputDataSize is null");
        checkArgument(inputPositions >= 0, "inputPositions is negative");
        this.inputPositions = inputPositions;
        this.sumSquaredInputPositions = sumSquaredInputPositions;

        this.getOutputCalls = getOutputCalls;
        this.getOutputWall = requireNonNull(getOutputWall, "getOutputWall is null");
        this.getOutputCpu = requireNonNull(getOutputCpu, "getOutputCpu is null");
        this.getOutputAllocation = requireNonNull(getOutputAllocation, "getOutputAllocation is null");
        this.outputDataSize = requireNonNull(outputDataSize, "outputDataSize is null");
        checkArgument(outputPositions >= 0, "outputPositions is negative");
        this.outputPositions = outputPositions;

        this.physicalWrittenDataSize = requireNonNull(physicalWrittenDataSize, "writtenDataSize is null");

        this.additionalCpu = requireNonNull(additionalCpu, "additionalCpu is null");
        this.blockedWall = requireNonNull(blockedWall, "blockedWall is null");

        this.finishCalls = finishCalls;
        this.finishWall = requireNonNull(finishWall, "finishWall is null");
        this.finishCpu = requireNonNull(finishCpu, "finishCpu is null");
        this.finishAllocation = requireNonNull(finishAllocation, "finishAllocation is null");

        this.userMemoryReservation = requireNonNull(userMemoryReservation, "userMemoryReservation is null");
        this.revocableMemoryReservation = requireNonNull(revocableMemoryReservation, "revocableMemoryReservation is null");
        this.systemMemoryReservation = requireNonNull(systemMemoryReservation, "systemMemoryReservation is null");

        this.peakUserMemoryReservation = requireNonNull(peakUserMemoryReservation, "peakUserMemoryReservation is null");
        this.peakSystemMemoryReservation = requireNonNull(peakSystemMemoryReservation, "peakSystemMemoryReservation is null");
        this.peakTotalMemoryReservation = requireNonNull(peakTotalMemoryReservation, "peakTotalMemoryReservation is null");

        this.spilledDataSize = requireNonNull(spilledDataSize, "spilledDataSize is null");

        this.runtimeStats = runtimeStats;

        this.blockedReason = blockedReason;

        this.infoUnion = infoUnion;
        this.info = null;
    }

    @JsonProperty
    @ThriftField(1)
    public int getStageId()
    {
        return stageId;
    }

    @JsonProperty
    @ThriftField(2)
    public int getStageExecutionId()
    {
        return stageExecutionId;
    }

    @JsonProperty
    @ThriftField(3)
    public int getPipelineId()
    {
        return pipelineId;
    }

    @JsonProperty
    @ThriftField(4)
    public int getOperatorId()
    {
        return operatorId;
    }

    @JsonProperty
    @ThriftField(5)
    public PlanNodeId getPlanNodeId()
    {
        return planNodeId;
    }

    @JsonProperty
    @ThriftField(6)
    public String getOperatorType()
    {
        return operatorType;
    }

    @JsonProperty
    @ThriftField(7)
    public long getTotalDrivers()
    {
        return totalDrivers;
    }

    @JsonProperty
    @ThriftField(8)
    public long getAddInputCalls()
    {
        return addInputCalls;
    }

    @JsonProperty
    @ThriftField(9)
    public Duration getAddInputWall()
    {
        return addInputWall;
    }

    @JsonProperty
    @ThriftField(10)
    public Duration getAddInputCpu()
    {
        return addInputCpu;
    }

    @JsonProperty
    @ThriftField(11)
    public DataSize getAddInputAllocation()
    {
        return addInputAllocation;
    }

    @JsonProperty
    @ThriftField(12)
    public DataSize getRawInputDataSize()
    {
        return rawInputDataSize;
    }

    @JsonProperty
    @ThriftField(13)
    public long getRawInputPositions()
    {
        return rawInputPositions;
    }

    @JsonProperty
    @ThriftField(14)
    public DataSize getInputDataSize()
    {
        return inputDataSize;
    }

    @JsonProperty
    @ThriftField(15)
    public long getInputPositions()
    {
        return inputPositions;
    }

    @JsonProperty
    @ThriftField(16)
    public double getSumSquaredInputPositions()
    {
        return sumSquaredInputPositions;
    }

    @JsonProperty
    @ThriftField(17)
    public long getGetOutputCalls()
    {
        return getOutputCalls;
    }

    @JsonProperty
    @ThriftField(18)
    public Duration getGetOutputWall()
    {
        return getOutputWall;
    }

    @JsonProperty
    @ThriftField(19)
    public Duration getGetOutputCpu()
    {
        return getOutputCpu;
    }

    @JsonProperty
    @ThriftField(20)
    public DataSize getGetOutputAllocation()
    {
        return getOutputAllocation;
    }

    @JsonProperty
    @ThriftField(21)
    public DataSize getOutputDataSize()
    {
        return outputDataSize;
    }

    @JsonProperty
    @ThriftField(22)
    public long getOutputPositions()
    {
        return outputPositions;
    }

    @JsonProperty
    @ThriftField(23)
    public DataSize getPhysicalWrittenDataSize()
    {
        return physicalWrittenDataSize;
    }

    @JsonProperty
    @ThriftField(24)
    public Duration getAdditionalCpu()
    {
        return additionalCpu;
    }

    @JsonProperty
    @ThriftField(25)
    public Duration getBlockedWall()
    {
        return blockedWall;
    }

    @JsonProperty
    @ThriftField(26)
    public long getFinishCalls()
    {
        return finishCalls;
    }

    @JsonProperty
    @ThriftField(27)
    public Duration getFinishWall()
    {
        return finishWall;
    }

    @JsonProperty
    @ThriftField(28)
    public Duration getFinishCpu()
    {
        return finishCpu;
    }

    @JsonProperty
    @ThriftField(29)
    public DataSize getFinishAllocation()
    {
        return finishAllocation;
    }

    @JsonProperty
    @ThriftField(30)
    public DataSize getUserMemoryReservation()
    {
        return userMemoryReservation;
    }

    @JsonProperty
    @ThriftField(31)
    public DataSize getRevocableMemoryReservation()
    {
        return revocableMemoryReservation;
    }

    @JsonProperty
    @ThriftField(32)
    public DataSize getSystemMemoryReservation()
    {
        return systemMemoryReservation;
    }

    @JsonProperty
    @ThriftField(33)
    public DataSize getPeakUserMemoryReservation()
    {
        return peakUserMemoryReservation;
    }

    @JsonProperty
    @ThriftField(34)
    public DataSize getPeakSystemMemoryReservation()
    {
        return peakSystemMemoryReservation;
    }

    @JsonProperty
    @ThriftField(35)
    public DataSize getPeakTotalMemoryReservation()
    {
        return peakTotalMemoryReservation;
    }

    @JsonProperty
    @ThriftField(36)
    public DataSize getSpilledDataSize()
    {
        return spilledDataSize;
    }

    @Nullable
    @JsonProperty
    @ThriftField(37)
    public RuntimeStats getRuntimeStats()
    {
        return runtimeStats;
    }

    @JsonProperty
    @ThriftField(38)
    public Optional<BlockedReason> getBlockedReason()
    {
        return blockedReason;
    }

    @Nullable
    @JsonProperty
    public OperatorInfo getInfo()
    {
        return info;
    }

    @ThriftField(39)
    public OperatorInfoUnion getInfoUnion()
    {
        return infoUnion;
    }

    public OperatorStats add(OperatorStats operatorStats)
    {
        return add(ImmutableList.of(operatorStats));
    }

    public OperatorStats add(Iterable<OperatorStats> operators)
    {
        long totalDrivers = this.totalDrivers;

        long addInputCalls = this.addInputCalls;
        long addInputWall = this.addInputWall.roundTo(NANOSECONDS);
        long addInputCpu = this.addInputCpu.roundTo(NANOSECONDS);
        long addInputAllocation = this.addInputAllocation.toBytes();
        long rawInputDataSize = this.rawInputDataSize.toBytes();
        long rawInputPositions = this.rawInputPositions;
        long inputDataSize = this.inputDataSize.toBytes();
        long inputPositions = this.inputPositions;
        double sumSquaredInputPositions = this.sumSquaredInputPositions;

        long getOutputCalls = this.getOutputCalls;
        long getOutputWall = this.getOutputWall.roundTo(NANOSECONDS);
        long getOutputCpu = this.getOutputCpu.roundTo(NANOSECONDS);
        long getOutputAllocation = this.getOutputAllocation.toBytes();
        long outputDataSize = this.outputDataSize.toBytes();
        long outputPositions = this.outputPositions;

        long physicalWrittenDataSize = this.physicalWrittenDataSize.toBytes();

        long additionalCpu = this.additionalCpu.roundTo(NANOSECONDS);
        long blockedWall = this.blockedWall.roundTo(NANOSECONDS);

        long finishCalls = this.finishCalls;
        long finishWall = this.finishWall.roundTo(NANOSECONDS);
        long finishCpu = this.finishCpu.roundTo(NANOSECONDS);
        long finishAllocation = this.finishAllocation.toBytes();

        long memoryReservation = this.userMemoryReservation.toBytes();
        long revocableMemoryReservation = this.revocableMemoryReservation.toBytes();
        long systemMemoryReservation = this.systemMemoryReservation.toBytes();
        long peakUserMemory = this.peakUserMemoryReservation.toBytes();
        long peakSystemMemory = this.peakSystemMemoryReservation.toBytes();
        long peakTotalMemory = this.peakTotalMemoryReservation.toBytes();

        long spilledDataSize = this.spilledDataSize.toBytes();

        Optional<BlockedReason> blockedReason = this.blockedReason;

        RuntimeStats runtimeStats = RuntimeStats.copyOf(this.runtimeStats);

        Mergeable<OperatorInfo> base = getMergeableInfoOrNull(info);
        for (OperatorStats operator : operators) {
            checkArgument(operator.getOperatorId() == operatorId, "Expected operatorId to be %s but was %s", operatorId, operator.getOperatorId());

            totalDrivers += operator.totalDrivers;

            addInputCalls += operator.getAddInputCalls();
            addInputWall += operator.getAddInputWall().roundTo(NANOSECONDS);
            addInputCpu += operator.getAddInputCpu().roundTo(NANOSECONDS);
            addInputAllocation += operator.getAddInputAllocation().toBytes();
            rawInputDataSize += operator.getRawInputDataSize().toBytes();
            rawInputPositions += operator.getRawInputPositions();
            inputDataSize += operator.getInputDataSize().toBytes();
            inputPositions += operator.getInputPositions();
            sumSquaredInputPositions += operator.getSumSquaredInputPositions();

            getOutputCalls += operator.getGetOutputCalls();
            getOutputWall += operator.getGetOutputWall().roundTo(NANOSECONDS);
            getOutputCpu += operator.getGetOutputCpu().roundTo(NANOSECONDS);
            getOutputAllocation += operator.getGetOutputAllocation().toBytes();
            outputDataSize += operator.getOutputDataSize().toBytes();
            outputPositions += operator.getOutputPositions();

            physicalWrittenDataSize += operator.getPhysicalWrittenDataSize().toBytes();

            finishCalls += operator.getFinishCalls();
            finishWall += operator.getFinishWall().roundTo(NANOSECONDS);
            finishCpu += operator.getFinishCpu().roundTo(NANOSECONDS);
            finishAllocation += operator.getFinishAllocation().toBytes();

            additionalCpu += operator.getAdditionalCpu().roundTo(NANOSECONDS);
            blockedWall += operator.getBlockedWall().roundTo(NANOSECONDS);

            memoryReservation += operator.getUserMemoryReservation().toBytes();
            revocableMemoryReservation += operator.getRevocableMemoryReservation().toBytes();
            systemMemoryReservation += operator.getSystemMemoryReservation().toBytes();

            peakUserMemory = max(peakUserMemory, operator.getPeakUserMemoryReservation().toBytes());
            peakSystemMemory = max(peakSystemMemory, operator.getPeakSystemMemoryReservation().toBytes());
            peakTotalMemory = max(peakTotalMemory, operator.getPeakTotalMemoryReservation().toBytes());

            spilledDataSize += operator.getSpilledDataSize().toBytes();

            if (operator.getBlockedReason().isPresent()) {
                blockedReason = operator.getBlockedReason();
            }

            OperatorInfo info = operator.getInfo();
            if (base != null && info != null && base.getClass() == info.getClass()) {
                base = mergeInfo(base, info);
            }

            runtimeStats.mergeWith(operator.getRuntimeStats());
        }

        return new OperatorStats(
                stageId,
                stageExecutionId,
                pipelineId,
                operatorId,
                planNodeId,
                operatorType,

                totalDrivers,

                addInputCalls,
                succinctNanos(addInputWall),
                succinctNanos(addInputCpu),
                succinctBytes(addInputAllocation),
                succinctBytes(rawInputDataSize),
                rawInputPositions,
                succinctBytes(inputDataSize),
                inputPositions,
                sumSquaredInputPositions,

                getOutputCalls,
                succinctNanos(getOutputWall),
                succinctNanos(getOutputCpu),
                succinctBytes(getOutputAllocation),
                succinctBytes(outputDataSize),
                outputPositions,

                succinctBytes(physicalWrittenDataSize),

                succinctNanos(additionalCpu),
                succinctNanos(blockedWall),

                finishCalls,
                succinctNanos(finishWall),
                succinctNanos(finishCpu),
                succinctBytes(finishAllocation),

                succinctBytes(memoryReservation),
                succinctBytes(revocableMemoryReservation),
                succinctBytes(systemMemoryReservation),
                succinctBytes(peakUserMemory),
                succinctBytes(peakSystemMemory),
                succinctBytes(peakTotalMemory),

                succinctBytes(spilledDataSize),

                blockedReason,

                (OperatorInfo) base,
                runtimeStats);
    }

    @SuppressWarnings("unchecked")
    private static Mergeable<OperatorInfo> getMergeableInfoOrNull(OperatorInfo info)
    {
        Mergeable<OperatorInfo> base = null;
        if (info instanceof Mergeable) {
            base = (Mergeable<OperatorInfo>) info;
        }
        return base;
    }

    @SuppressWarnings("unchecked")
    private static <T> Mergeable<T> mergeInfo(Mergeable<T> base, T other)
    {
        return (Mergeable<T>) base.mergeWith(other);
    }

    public OperatorStats summarize()
    {
        if (info == null || info.isFinal()) {
            return this;
        }
        OperatorInfo info = null;
        return new OperatorStats(
                stageId,
                stageExecutionId,
                pipelineId,
                operatorId,
                planNodeId,
                operatorType,
                totalDrivers,
                addInputCalls,
                addInputWall,
                addInputCpu,
                addInputAllocation,
                rawInputDataSize,
                rawInputPositions,
                inputDataSize,
                inputPositions,
                sumSquaredInputPositions,
                getOutputCalls,
                getOutputWall,
                getOutputCpu,
                getOutputAllocation,
                outputDataSize,
                outputPositions,
                physicalWrittenDataSize,
                additionalCpu,
                blockedWall,
                finishCalls,
                finishWall,
                finishCpu,
                finishAllocation,
                userMemoryReservation,
                revocableMemoryReservation,
                systemMemoryReservation,
                peakUserMemoryReservation,
                peakSystemMemoryReservation,
                peakTotalMemoryReservation,
                spilledDataSize,
                blockedReason,
                info,
                runtimeStats);
    }
}
