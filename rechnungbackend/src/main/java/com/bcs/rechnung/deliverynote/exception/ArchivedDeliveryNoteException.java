package com.bcs.rechnung.deliverynote.exception;

public class ArchivedDeliveryNoteException
    extends RuntimeException {

    public ArchivedDeliveryNoteException(
        String deliveryNoteNumber
    ) {
        super(
            "Der Lieferschein "
                + deliveryNoteNumber
                + " ist bereits archiviert und darf "
                + "nicht mehr geändert oder gelöscht werden."
        );
    }
}
