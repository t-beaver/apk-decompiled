package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.image.EncodedImage;
import javax.annotation.Nullable;

public class AddImageTransformMetaDataProducer implements Producer<EncodedImage> {
    private final Producer<EncodedImage> mInputProducer;

    public AddImageTransformMetaDataProducer(Producer<EncodedImage> producer) {
        this.mInputProducer = producer;
    }

    public void produceResults(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
        this.mInputProducer.produceResults(new AddImageTransformMetaDataConsumer(consumer), producerContext);
    }

    private static class AddImageTransformMetaDataConsumer extends DelegatingConsumer<EncodedImage, EncodedImage> {
        private AddImageTransformMetaDataConsumer(Consumer<EncodedImage> consumer) {
            super(consumer);
        }

        /* access modifiers changed from: protected */
        public void onNewResultImpl(@Nullable EncodedImage encodedImage, int i) {
            if (encodedImage == null) {
                getConsumer().onNewResult(null, i);
                return;
            }
            if (!EncodedImage.isMetaDataAvailable(encodedImage)) {
                encodedImage.parseMetaData();
            }
            getConsumer().onNewResult(encodedImage, i);
        }
    }
}
