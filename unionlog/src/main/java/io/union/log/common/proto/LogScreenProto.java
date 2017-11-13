// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: LogScreen.proto

package io.union.log.common.proto;

public final class LogScreenProto {
    private LogScreenProto() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    public interface LogScreenMessageOrBuilder extends
            // @@protoc_insertion_point(interface_extends:io.union.log.common.proto.LogScreenMessage)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>uint64 id = 1;</code>
         */
        long getId();

        /**
         * <code>string cus_screen = 2;</code>
         */
        java.lang.String getCusScreen();

        /**
         * <code>string cus_screen = 2;</code>
         */
        com.google.protobuf.ByteString
        getCusScreenBytes();

        /**
         * <code>uint64 cus_num = 3;</code>
         */
        long getCusNum();

        /**
         * <code>uint64 uid = 4;</code>
         */
        long getUid();

        /**
         * <code>uint64 add_time = 5;</code>
         */
        long getAddTime();
    }

    /**
     * Protobuf type {@code io.union.log.common.proto.LogScreenMessage}
     */
    public static final class LogScreenMessage extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:io.union.log.common.proto.LogScreenMessage)
            LogScreenMessageOrBuilder {
        // Use LogScreenMessage.newBuilder() to construct.
        private LogScreenMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private LogScreenMessage() {
            id_ = 0L;
            cusScreen_ = "";
            cusNum_ = 0L;
            uid_ = 0L;
            addTime_ = 0L;
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }

        private LogScreenMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {

                            id_ = input.readUInt64();
                            break;
                        }
                        case 18: {
                            java.lang.String s = input.readStringRequireUtf8();

                            cusScreen_ = s;
                            break;
                        }
                        case 24: {

                            cusNum_ = input.readUInt64();
                            break;
                        }
                        case 32: {

                            uid_ = input.readUInt64();
                            break;
                        }
                        case 40: {

                            addTime_ = input.readUInt64();
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return io.union.log.common.proto.LogScreenProto.internal_static_io_union_log_common_proto_LogScreenMessage_descriptor;
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return io.union.log.common.proto.LogScreenProto.internal_static_io_union_log_common_proto_LogScreenMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            io.union.log.common.proto.LogScreenProto.LogScreenMessage.class, io.union.log.common.proto.LogScreenProto.LogScreenMessage.Builder.class);
        }

        public static final int ID_FIELD_NUMBER = 1;
        private long id_;

        /**
         * <code>uint64 id = 1;</code>
         */
        public long getId() {
            return id_;
        }

        public static final int CUS_SCREEN_FIELD_NUMBER = 2;
        private volatile java.lang.Object cusScreen_;

        /**
         * <code>string cus_screen = 2;</code>
         */
        public java.lang.String getCusScreen() {
            java.lang.Object ref = cusScreen_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                cusScreen_ = s;
                return s;
            }
        }

        /**
         * <code>string cus_screen = 2;</code>
         */
        public com.google.protobuf.ByteString
        getCusScreenBytes() {
            java.lang.Object ref = cusScreen_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                cusScreen_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int CUS_NUM_FIELD_NUMBER = 3;
        private long cusNum_;

        /**
         * <code>uint64 cus_num = 3;</code>
         */
        public long getCusNum() {
            return cusNum_;
        }

        public static final int UID_FIELD_NUMBER = 4;
        private long uid_;

        /**
         * <code>uint64 uid = 4;</code>
         */
        public long getUid() {
            return uid_;
        }

        public static final int ADD_TIME_FIELD_NUMBER = 5;
        private long addTime_;

        /**
         * <code>uint64 add_time = 5;</code>
         */
        public long getAddTime() {
            return addTime_;
        }

        private byte memoizedIsInitialized = -1;

        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (id_ != 0L) {
                output.writeUInt64(1, id_);
            }
            if (!getCusScreenBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 2, cusScreen_);
            }
            if (cusNum_ != 0L) {
                output.writeUInt64(3, cusNum_);
            }
            if (uid_ != 0L) {
                output.writeUInt64(4, uid_);
            }
            if (addTime_ != 0L) {
                output.writeUInt64(5, addTime_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (id_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt64Size(1, id_);
            }
            if (!getCusScreenBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, cusScreen_);
            }
            if (cusNum_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt64Size(3, cusNum_);
            }
            if (uid_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt64Size(4, uid_);
            }
            if (addTime_ != 0L) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt64Size(5, addTime_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;

        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof io.union.log.common.proto.LogScreenProto.LogScreenMessage)) {
                return super.equals(obj);
            }
            io.union.log.common.proto.LogScreenProto.LogScreenMessage other = (io.union.log.common.proto.LogScreenProto.LogScreenMessage) obj;

            boolean result = true;
            result = result && (getId()
                    == other.getId());
            result = result && getCusScreen()
                    .equals(other.getCusScreen());
            result = result && (getCusNum()
                    == other.getCusNum());
            result = result && (getUid()
                    == other.getUid());
            result = result && (getAddTime()
                    == other.getAddTime());
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + ID_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getId());
            hash = (37 * hash) + CUS_SCREEN_FIELD_NUMBER;
            hash = (53 * hash) + getCusScreen().hashCode();
            hash = (37 * hash) + CUS_NUM_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getCusNum());
            hash = (37 * hash) + UID_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getUid());
            hash = (37 * hash) + ADD_TIME_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                    getAddTime());
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(io.union.log.common.proto.LogScreenProto.LogScreenMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code io.union.log.common.proto.LogScreenMessage}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:io.union.log.common.proto.LogScreenMessage)
                io.union.log.common.proto.LogScreenProto.LogScreenMessageOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return io.union.log.common.proto.LogScreenProto.internal_static_io_union_log_common_proto_LogScreenMessage_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return io.union.log.common.proto.LogScreenProto.internal_static_io_union_log_common_proto_LogScreenMessage_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                io.union.log.common.proto.LogScreenProto.LogScreenMessage.class, io.union.log.common.proto.LogScreenProto.LogScreenMessage.Builder.class);
            }

            // Construct using io.union.log.common.proto.LogScreenProto.LogScreenMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }

            public Builder clear() {
                super.clear();
                id_ = 0L;

                cusScreen_ = "";

                cusNum_ = 0L;

                uid_ = 0L;

                addTime_ = 0L;

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return io.union.log.common.proto.LogScreenProto.internal_static_io_union_log_common_proto_LogScreenMessage_descriptor;
            }

            public io.union.log.common.proto.LogScreenProto.LogScreenMessage getDefaultInstanceForType() {
                return io.union.log.common.proto.LogScreenProto.LogScreenMessage.getDefaultInstance();
            }

            public io.union.log.common.proto.LogScreenProto.LogScreenMessage build() {
                io.union.log.common.proto.LogScreenProto.LogScreenMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public io.union.log.common.proto.LogScreenProto.LogScreenMessage buildPartial() {
                io.union.log.common.proto.LogScreenProto.LogScreenMessage result = new io.union.log.common.proto.LogScreenProto.LogScreenMessage(this);
                result.id_ = id_;
                result.cusScreen_ = cusScreen_;
                result.cusNum_ = cusNum_;
                result.uid_ = uid_;
                result.addTime_ = addTime_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }

            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.setField(field, value);
            }

            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof io.union.log.common.proto.LogScreenProto.LogScreenMessage) {
                    return mergeFrom((io.union.log.common.proto.LogScreenProto.LogScreenMessage) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(io.union.log.common.proto.LogScreenProto.LogScreenMessage other) {
                if (other == io.union.log.common.proto.LogScreenProto.LogScreenMessage.getDefaultInstance())
                    return this;
                if (other.getId() != 0L) {
                    setId(other.getId());
                }
                if (!other.getCusScreen().isEmpty()) {
                    cusScreen_ = other.cusScreen_;
                    onChanged();
                }
                if (other.getCusNum() != 0L) {
                    setCusNum(other.getCusNum());
                }
                if (other.getUid() != 0L) {
                    setUid(other.getUid());
                }
                if (other.getAddTime() != 0L) {
                    setAddTime(other.getAddTime());
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                io.union.log.common.proto.LogScreenProto.LogScreenMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (io.union.log.common.proto.LogScreenProto.LogScreenMessage) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private long id_;

            /**
             * <code>uint64 id = 1;</code>
             */
            public long getId() {
                return id_;
            }

            /**
             * <code>uint64 id = 1;</code>
             */
            public Builder setId(long value) {

                id_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>uint64 id = 1;</code>
             */
            public Builder clearId() {

                id_ = 0L;
                onChanged();
                return this;
            }

            private java.lang.Object cusScreen_ = "";

            /**
             * <code>string cus_screen = 2;</code>
             */
            public java.lang.String getCusScreen() {
                java.lang.Object ref = cusScreen_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    cusScreen_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string cus_screen = 2;</code>
             */
            public com.google.protobuf.ByteString
            getCusScreenBytes() {
                java.lang.Object ref = cusScreen_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    cusScreen_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string cus_screen = 2;</code>
             */
            public Builder setCusScreen(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                cusScreen_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string cus_screen = 2;</code>
             */
            public Builder clearCusScreen() {

                cusScreen_ = getDefaultInstance().getCusScreen();
                onChanged();
                return this;
            }

            /**
             * <code>string cus_screen = 2;</code>
             */
            public Builder setCusScreenBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                cusScreen_ = value;
                onChanged();
                return this;
            }

            private long cusNum_;

            /**
             * <code>uint64 cus_num = 3;</code>
             */
            public long getCusNum() {
                return cusNum_;
            }

            /**
             * <code>uint64 cus_num = 3;</code>
             */
            public Builder setCusNum(long value) {

                cusNum_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>uint64 cus_num = 3;</code>
             */
            public Builder clearCusNum() {

                cusNum_ = 0L;
                onChanged();
                return this;
            }

            private long uid_;

            /**
             * <code>uint64 uid = 4;</code>
             */
            public long getUid() {
                return uid_;
            }

            /**
             * <code>uint64 uid = 4;</code>
             */
            public Builder setUid(long value) {

                uid_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>uint64 uid = 4;</code>
             */
            public Builder clearUid() {

                uid_ = 0L;
                onChanged();
                return this;
            }

            private long addTime_;

            /**
             * <code>uint64 add_time = 5;</code>
             */
            public long getAddTime() {
                return addTime_;
            }

            /**
             * <code>uint64 add_time = 5;</code>
             */
            public Builder setAddTime(long value) {

                addTime_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>uint64 add_time = 5;</code>
             */
            public Builder clearAddTime() {

                addTime_ = 0L;
                onChanged();
                return this;
            }

            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:io.union.log.common.proto.LogScreenMessage)
        }

        // @@protoc_insertion_point(class_scope:io.union.log.common.proto.LogScreenMessage)
        private static final io.union.log.common.proto.LogScreenProto.LogScreenMessage DEFAULT_INSTANCE;

        static {
            DEFAULT_INSTANCE = new io.union.log.common.proto.LogScreenProto.LogScreenMessage();
        }

        public static io.union.log.common.proto.LogScreenProto.LogScreenMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<LogScreenMessage>
                PARSER = new com.google.protobuf.AbstractParser<LogScreenMessage>() {
            public LogScreenMessage parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new LogScreenMessage(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<LogScreenMessage> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<LogScreenMessage> getParserForType() {
            return PARSER;
        }

        public io.union.log.common.proto.LogScreenProto.LogScreenMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_io_union_log_common_proto_LogScreenMessage_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_io_union_log_common_proto_LogScreenMessage_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor
            descriptor;

    static {
        java.lang.String[] descriptorData = {
                "\n\017LogScreen.proto\022\031io.union.log.common.p" +
                        "roto\"b\n\020LogScreenMessage\022\n\n\002id\030\001 \001(\004\022\022\n\n" +
                        "cus_screen\030\002 \001(\t\022\017\n\007cus_num\030\003 \001(\004\022\013\n\003uid" +
                        "\030\004 \001(\004\022\020\n\010add_time\030\005 \001(\004B+\n\031io.union.log" +
                        ".common.protoB\016LogScreenProtob\006proto3"
        };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
                new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
                    public com.google.protobuf.ExtensionRegistry assignDescriptors(
                            com.google.protobuf.Descriptors.FileDescriptor root) {
                        descriptor = root;
                        return null;
                    }
                };
        com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[]{
                        }, assigner);
        internal_static_io_union_log_common_proto_LogScreenMessage_descriptor =
                getDescriptor().getMessageTypes().get(0);
        internal_static_io_union_log_common_proto_LogScreenMessage_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_io_union_log_common_proto_LogScreenMessage_descriptor,
                new java.lang.String[]{"Id", "CusScreen", "CusNum", "Uid", "AddTime",});
    }

    // @@protoc_insertion_point(outer_class_scope)
}