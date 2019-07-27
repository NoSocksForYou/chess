package chess.piece;

import chess.board.Board;
import chess.misc.exceptions.ChessException;
import chess.misc.Position;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.LinkedHashSet;
import java.util.Set;

public class Pawn extends Piece {


    public Pawn (PieceColor color) {
        super(PieceType.PAWN, color, color == PieceColor.WHITE ? "♙" : "♟", 1);
    }

    @Override
    public Set<Position> getPossiblePositions (Board board, Position position) {
        Set<Position> positions = new LinkedHashSet<>();

        positions.addAll(handleStraightAhead(board, position));
        positions.addAll(handleCapture(board, position));
        positions.addAll(handleEnPassant(board, position));

        return positions;
    }

    private Set<Position> handleStraightAhead (Board board, Position position) {
        Set<Position> positions = new LinkedHashSet<>();
        try {
            if (board.isSquareEmpty(position.offsetY(getForwardDirection()))) {
                positions.add(position.offsetY(getForwardDirection()));

                try {
                    if (!hasMoved() && board.isSquareEmpty(position.offsetY(getForwardDirection() * 2))) {
                        positions.add(position.offsetY(getForwardDirection() * 2));
                    }
                } catch (ChessException ignored) {}
            }
        } catch (ChessException ignored) {}

        return positions;
    }

    private Set<Position> handleCapture (Board board, Position position) {
        Set<Position> positions = new LinkedHashSet<>();

        try {
            if (board.getPieceInSquare(position.offset(1, getForwardDirection())).getColor() == color.invert()) {
                positions.add(position.offset(1, getForwardDirection()));
            }
        } catch (ChessException ignored) {}

        try {
            if (board.getPieceInSquare(position.offset(-1, getForwardDirection())).getColor() == color.invert()) {
                positions.add(position.offset(-1, getForwardDirection()));
            }
        } catch (ChessException ignored) {}

        return positions;
    }

    private Set<Position> handleEnPassant (Board board, Position position) {
        Set<Position> positions = new LinkedHashSet<>();

        try {
            Piece piece = board.getPieceInSquare(position.offsetX(1));
            if (piece instanceof Pawn && ((Pawn) piece).isEligibleForEnPassant() && piece.getColor() == color.invert()) {
                if (board.isSquareEmpty(position.offset(1, getForwardDirection()))) {
                    positions.add(position.offset(1, getForwardDirection()));
                }
            }
        } catch (ChessException ignored) {}

        try {
            Piece piece = board.getPieceInSquare(position.offsetX(-1));
            if (piece instanceof Pawn && ((Pawn) piece).isEligibleForEnPassant() && piece.getColor() == color.invert()) {
                if (board.isSquareEmpty(position.offset(-1, getForwardDirection()))) {
                    positions.add(position.offset(-1, getForwardDirection()));
                }
            }
        } catch (ChessException ignored) {}

        return positions;
    }

    private boolean isEligibleForEnPassant () {
        return getLastMove() != null && Math.abs(getLastMove().getOrigin().getY() - getLastMove().getDestination().getY()) == 2;
    }

    @Override
    public int getIndex (Board board, Position position) {
        if (handleEnPassant(board, position).size() > 0) {
            return getColor() == PieceColor.WHITE ? 12 : 13;
        } else {
            return super.getIndex(board, position);
        }
    }
}
