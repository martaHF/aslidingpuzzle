package ar.com.linuxwarrior;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class ASPGameActivity extends Activity {
	public class PuzzleGame {
		private Integer[] mPuzzleSolved;
		private int mBlackX; 
		private int mBlackY; 
		
		public PuzzleGame() {
			// Just this set of pieces for now
			mPuzzleSolved = new Integer[mNumCols * mNumCols];			
			System.arraycopy( mThumbIds, 0, mPuzzleSolved, 0, mThumbIds.length );
			mBlackX=mBlackY=2;
			mJustLoaded = true;
		}
		
		public void getBoardStatus() {
			int size = mNumCols * mNumCols;
			
			mPuzzleSolved = new Integer[size];

			for (int i=0 ; i<size-1 ;i++) {
				mPuzzleSolved[i]=mPrefs.getInt("piece_"+i, 0);
				mThumbIds[i]=mPrefs.getInt("statusPiece_"+i, 0);
			}
			mBlackX=mPrefs.getInt("black_X", mNumCols-1);
			mBlackY=mPrefs.getInt("black_Y", mNumCols-1);
			
		}

		public void saveBoardStatus() {
			int size = mNumCols * mNumCols;

			SharedPreferences.Editor ed = mPrefs.edit();
	        ed.putInt("black_X", mBlackX);
	        ed.putInt("black_Y", mBlackY);

	        for (int i=0 ; i<size-1 ;i++) {
				ed.putInt("piece_"+i, mPuzzleSolved[i]);
				ed.putInt("statusPiece_"+i, mThumbIds[i]);
			}

			ed.commit();
		}

		public boolean isValidMove(int position) {
			int x = position % mNumCols;
			int y = position / mNumCols;
			
			if ( position <0 || position > mNumCols*mNumCols -1) return false;
			
			if (( Math.abs(x-mBlackX)+ Math.abs(y-mBlackY)) == 1 ) {
				return true;
			} else {
				return false;
			}
		}
		
		public void swapPieces(int position) {
			
			int tmpId = mThumbIds[position];
			mThumbIds[position]=0;
			mThumbIds[mBlackY*mNumCols+mBlackX]=tmpId;
			mBlackX= position % mNumCols;
			mBlackY= position / mNumCols;
			
		}

		public int getBlackPos() {
			return mBlackY*mNumCols+mBlackX;
		}

		public boolean didWin() {
			return Arrays.equals(mThumbIds, mPuzzleSolved);
		}

		public void shuffle() {
			int numSwaps = (int)(Math.random()*100)+15;
			for (int i=0 ; i<numSwaps ; i++) {
				int startPos=getBlackPos();
				int direction = (int)Math.round(Math.random()*3);
				int pos=0;
				switch (direction) {
					case 0:
						pos=startPos+1;
						break;
					case 1:
						pos=startPos-1;
						break;
					case 2:
						pos=startPos+mNumCols;
						break;
					case 3:
						pos=startPos-mNumCols;
						break;
					default: break;
				}
            	if( game.isValidMove(pos) ) {
            		swapPieces(pos);
            	}

			}
		}
		
	}

	
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;

	    public ImageAdapter(Context c) {
	        mContext = c;
	    }

	    public int getCount() {
	        return mThumbIds.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(mImgWidth, mImgWidth));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(1, 1, 1, 1);
	            imageView.setOnTouchListener(new View.OnTouchListener() {
	                public boolean onTouch(View v, MotionEvent event) {
	                	if( event.getAction() == MotionEvent.ACTION_UP) {
		                	int position = (int) (v.getTop()/mImgWidth) * mNumCols + (int) (v.getLeft()/mImgWidth);
		                	if( game.isValidMove(position) ) {
			        			int blackPos=game.getBlackPos();
		                		game.swapPieces(position);

			        			GridView gridview = (GridView) findViewById(R.id.gridview);
			        			((ImageView)gridview.getChildAt(position)).setImageResource(mThumbIds[position]);
			        			((ImageView)gridview.getChildAt(blackPos)).setImageResource(mThumbIds[blackPos]);
			        			if( game.didWin() ) {
			        				// Show Congratulations dialog
			        				showDialog(DIALOG_YOUWIN_ID);
			        				// Close activity
			        			}
		                	}
		                }
	                	return true;
	                }
	            });
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageResource(mThumbIds[position]);
	        return imageView;
	    }

	}
	static final int DIALOG_PAUSED_ID = 0;
	static final int DIALOG_YOUWIN_ID = 1;
	
    private SharedPreferences mPrefs;
    private int mNumCols= 3;
    private int mImgWidth= 85;
    // references to our images
    private Integer[] mThumbIds = {
    		R.drawable.p1, R.drawable.p2,
    		R.drawable.p3, R.drawable.p4,
    		R.drawable.p5, R.drawable.p6,
    		R.drawable.p7, R.drawable.p8,
    		0
    };
    boolean mJustLoaded = false;
    private PuzzleGame game = new PuzzleGame();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);		

		mPrefs = getSharedPreferences("game_config", MODE_PRIVATE);
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this));

	    mNumCols=mPrefs.getInt("puzzle_size", 0)+3;
	    gridview.setNumColumns(mNumCols);
	    if (!mJustLoaded) {
	    	game.getBoardStatus();
	    } else {
	    	game.shuffle();
	    	mJustLoaded=false;
	    }

	    mImgWidth = getResources().getDisplayMetrics().widthPixels / mNumCols - 2;
	}
	
	@Override
    protected void onPause() {
        super.onPause();

        game.saveBoardStatus();
	}

	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    switch(id) {
	    case DIALOG_PAUSED_ID:
	        // do the work to define the pause Dialog
	        break;
	    case DIALOG_YOUWIN_ID:
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("You WIN !!!, Play Again?")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                ASPGameActivity.this.finish();
	    	           }
	    	       })
	    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                ASPGameActivity.this.finish();
	    	           }
	    	       });
	    	dialog = builder.create();
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}

	
}
