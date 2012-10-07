/*
 * Copyright (c) 2009-2011 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package eu.infomas.dropbox;

/**
 * Represents the size of thumb nails that the API can return.
 * 
 * @author Original Author is Dropbox
 */
public enum ThumbSize {

    /**
     * Will have at most a 32 width or 32 height, maintaining its original aspect ratio.
     */
    ICON_32x32("small"),
    
    /**
     * 64 width or 64 height, with original aspect ratio.
     */
    ICON_64x64("medium"),
    
    /**
     * 128 width or 128 height, with original aspect ratio.
     */
    ICON_128x128("large"),
    
    /**
     * 256 width or 256 height, with original aspect ratio.
     */
    ICON_256x256("256x256"),
    
    /**
     * Will either fit within a 320 x 240 rectangle or a 240 x 320 rectangle, whichever
     * results in a larger image.
     */
    BESTFIT_320x240("320x240_bestfit"),
    
    /**
     * Fits within 480x320 or 320x480
     */
    BESTFIT_480x320("480x320_bestfit"),
    
    /**
     * Fits within 640x480 or 480x640
     */
    BESTFIT_640x480("640x480_bestfit"),
    
    /**
     * Fits within 960x640 or 640x960
     */
    BESTFIT_960x640("960x640_bestfit"),
    
    /**
     * Fits within 1024x768 or 768x1024
     */
    BESTFIT_1024x768("1024x768_bestfit");
    
    private final String size;

    private ThumbSize(final String size) {
        this.size = size;
    }

    /**
     * Return the identifier if this size, used in the REST API Request.
     */
    public String toAPISize() {
        return size;
    }
}